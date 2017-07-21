package network.marble.moderationslave.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
        if (args.length > 1 || args.length < 1) {
            sender.sendMessage(FontFormat.translateString("&cIncorrect arguments! Usage: /(p|punish|punishment) (player)"));
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(FontFormat.translateString("&c" + args[0] + " is either offline or not valid player!"));
            return false;
        }

        PunishmentAPI.getPunishExecutions().forEach(a -> a.executeAction(target, null, null));

        Chunk startingChunk = target.getWorld().getChunkAt(target.getLocation().getBlockX(), target.getLocation().getBlockZ());
        int startX = startingChunk.getX() / 16;
        int startZ = startingChunk.getZ() / 16;

        // Lag
        int startY = 5;

        Schematic schematic = ModerationSlave.getSchematic();

        target.teleport(new Location(target.getWorld(),
                startX * 16 - schematic.getOffset().getBlockX(),
                startY - schematic.getOffset().getBlockY(),
                startZ * 16 - schematic.getOffset().getBlockZ()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(target)) continue;
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(
                    new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle()));
        }
        target.setGameMode(GameMode.ADVENTURE);
        target.getInventory().clear();

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

                                if(m != null ) {
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

            Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> {
                target.kickPlayer("KickedForPunishment");
            });
        }).start();

        return false;
    }
}
