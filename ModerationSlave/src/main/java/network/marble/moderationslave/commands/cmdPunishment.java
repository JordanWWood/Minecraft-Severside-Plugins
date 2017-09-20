package network.marble.moderationslave.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messagelibrary.api.MessageLibrary;
import network.marble.messagelibrary.api.MessageType;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.api.PunishmentAPI;
import network.marble.moderationslave.utils.FontFormat;
import network.marble.moderationslave.utils.Schematic;

public class cmdPunishment implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        User u = null;
        try {
            u = new User().getByUUID(((Player) sender).getUniqueId());
        } catch (APIException e) {
            e.printStackTrace();
        }

        if (u.hasPermission("moderation.judgement")) {
            if (args.length > 1 || args.length < 1) {
                sender.sendMessage(FontFormat.translateString("&cIncorrect arguments! Usage: /(p|punish|punishment) (player)"));
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(FontFormat.translateString("&c" + args[0] + " is either offline or not valid player!"));
                return false;
            }

            limboPlayer(target, u);
        }
        return false;
    }

    public static void limboPlayer(Player target, User sender) {
        PunishmentAPI.getPunishExecutions().forEach(a -> a.executeAction(target, null, null));

        Chunk startingChunk = target.getWorld().getChunkAt(target.getLocation().getBlockX(), target.getLocation().getBlockZ());
        int startX = startingChunk.getX() / 16;
        int startZ = startingChunk.getZ() / 16;

        // Lag
        int startY = 5;

        Schematic schematic = ModerationSlave.getSchematic();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(target)) continue;
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(
                    new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle()));
        }
        target.setGameMode(GameMode.ADVENTURE);

        new Thread(() -> {
            try {
                Thread.currentThread().sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Schematic preparation
            byte[][][] block = new byte[schematic.getWidth()][schematic.getHeight()][schematic.getLength()];
            byte[][][] data = new byte[schematic.getWidth()][schematic.getHeight()][schematic.getLength()];

            int index = 0;
            for (int y = 0; y < schematic.getHeight(); y++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    for (int x = 0; x < schematic.getWidth(); x++) {
                        block[x][y][z] = schematic.getBlocks()[index];
                        data[x][y][z] = schematic.getData()[index];

                        index++;
                    }
                }
            }
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();

            //Calculate the chunk change packets
            for (int cX = 0; cX < (schematic.getWidth() / 16) + (schematic.getWidth() % 16 > 0 ? 1 : 0); cX++) {
                for (int cZ = 0; cZ < (schematic.getLength() / 16) + (schematic.getLength() % 16 > 0 ? 1 : 0); cZ++) {
                    ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(startX + cX, startZ + cZ);
                    ArrayList<MultiBlockChangeInfo> changes = new ArrayList<>();
                    PacketContainer packet = pm.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);

                    for (int x = cX * 16; x < (cX * 16) + (schematic.getWidth() - (cX * 16) > 16 ? 16 : schematic.getWidth() - (cX * 16)); x++) {
                        for (int z = cZ * 16; z < (cZ * 16) + (schematic.getLength() - (cZ * 16) > 16 ? 16 : schematic.getLength() - (cZ * 16)); z++) {
                            for (int y = 0; y < schematic.getHeight(); y++) {

                                Material m = Material.getMaterial(((int) block[x][y][z] & 0xFF));

                                if (m != null) {
                                    changes.add(new MultiBlockChangeInfo(
                                            new Location(target.getWorld(), (startX + cX) * 16 + x, y + startY, (startZ + cZ) * 16 + z),
                                            WrappedBlockData.createData(m, ((int) data[x][y][z] & 0xFF))));
                                }
                            }
                        }
                    }

                    try {
                        packet.getChunkCoordIntPairs().write(0, chunkCoords);
                        MultiBlockChangeInfo[] change = new MultiBlockChangeInfo[changes.size()];
                        change = changes.toArray(change);
                        packet.getMultiBlockChangeInfoArrays().write(0, change);

                        pm.sendServerPacket(target, packet);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            UUID caseID = null;
            try {
                User u = new User().getByUUID(target.getUniqueId());
                Case c = new Case();

                List<Case> cases = new Case().getOpenByJudgementee(u.getId());
                for (Case ca : cases) {
                    if (ca.isPardoned()) continue;
                    if (ca.getJudgement_session_id() != null) {
                        ModerationSlave.getInstance().getLogger().info("Judgement ID not null");
                        caseID = ca.getId();
                        break;
                    }
                }

                if (caseID == null) {
                    if (sender != null) {
                        c.setAssignee_id(sender.getId());
                        c.setCreated_by(sender.getId());
                    }

                    c.setCreated_at(System.currentTimeMillis());
                    c.setJudgementee_id(u.getId());
                    caseID = c.saveAndReturn().getId();
                }
            } catch (APIException e) {
                e.printStackTrace();
            }

            UUID finalCaseID = caseID;
            Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> {
                target.teleport(new Location(target.getWorld(),
                        startX * 16 - schematic.getOffset().getBlockX(),
                        startY - schematic.getOffset().getBlockY(),
                        startZ * 16 - schematic.getOffset().getBlockZ()));

                if(target.isGliding()) target.setGliding(false);

                if (sender != null) {
                    TextComponent message = new TextComponent(MessageType.INFO.prefix + target.getName() + " has been moved. Complete the form on the ");
                    TextComponent link = new TextComponent(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "PANEL");
                    link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://panel.marble.network/cases/" + finalCaseID));
                    link.setUnderlined(true);

                    message.addExtra(link);
                    message.addExtra(MessageType.INFO.prefix + " to " + MessageType.INFO.prefix + "finalise the player's punishment");

                    Bukkit.getPlayer(sender.getUuid()).spigot().sendMessage(message);
                }

                target.kickPlayer("KickedForPunishment-" + finalCaseID);
            });
        }).start();
    }
}
