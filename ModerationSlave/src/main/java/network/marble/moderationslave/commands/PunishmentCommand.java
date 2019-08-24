package network.marble.moderationslave.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import lombok.Getter;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.dataaccesslayer.models.plugins.moderation.Punishment;
import network.marble.dataaccesslayer.models.user.Moderation;
import network.marble.messageapi.api.MessageAPI;
import network.marble.messagelibrary.api.Lang;
import network.marble.moderationslave.communication.messages.PunishmentRequest;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.api.PunishmentAPI;
import network.marble.moderationslave.utils.Schematic;

public class PunishmentCommand implements CommandExecutor {
    @Getter
    private static Map<String, Boolean> await = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(ModerationSlave.getInstance(), () -> {
            User u = null;
            Player p = (Player) sender;
            try {
                u = new User().get(p.getUniqueId());
            } catch (APIException e) {
                Lang.chat("general.perm", p);

                e.printStackTrace();
                return;
            }

            if (!u.hasPermission("moderation.judgement")) {
                Lang.chat("general.perm", p);
                return;
            }

            if (args.length > 2 || args.length < 1) {
                Lang.chat("mod.punish.args", p);
                return;
            }

            User targetUser;
            try {
                targetUser = new User().getByUsername(args[0]);
            } catch (APIException e) {
                e.printStackTrace();
                return;
            }

            if (targetUser.getUuid() == p.getUniqueId()) {
                Lang.chat("mod.punish.self", p);
                return;
            }

            if (args.length >= 2) {
                executePunishmentPipeline(u, args[0], args[1]);
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                PunishmentRequest r = new PunishmentRequest(args[0], "", p.getUniqueId(), null);
                r.sendMessage("mslave.player." + args[0]);

                return;
            }

            limboPlayer(target, u, null);
        });

        return true;
    }

    public static void executePunishmentPipeline(User u, String targetName, String pipeline) {
        try {
            User target = new User().getByUsername(targetName);

            // Check if specified pipeline exists and if it does check the index
            List<Punishment> punishments = new Punishment().get();
            boolean match = false;
            int index = 0;
            for (Punishment punishment : punishments) {
                if (punishment.getOffense().equalsIgnoreCase(pipeline)) {
                    match = true;
                    break;
                }

                index++;
            }

            // If the pipeline doesnt exist notify the user
            if (!match) {
                MessageAPI.sendMessage(null, u.getUuid(), "mod.cross.punishment.args", true);
                for (Punishment punishment : punishments)
                    MessageAPI.sendMessage(null, u.getUuid(), punishment.getOffense(), false);

                return;
            }

            // Check which stage of the pipeline the target is on. This is done by getting active cases for a give player
            // and checking if they are associated with the given pipeline
            int step = 0;
            CaseOutcome outcome = CaseOutcome.Warn;
            for (Punishment.RepeatedAction action : punishments.get(index).getRepeat_actions()) {
                switch (action.getAction()) {
                    case WARN: outcome = CaseOutcome.Warn; break;
                    case KICK: outcome = CaseOutcome.Kicked; break;
                    case MUTE: outcome = CaseOutcome.Muted; break;
                    case TEMP_BAN: outcome = CaseOutcome.TemporaryBan; break;
                    case PERM_BAN: outcome = CaseOutcome.PermanentlyBan; break;
                }

                List<Case> cases = new Case().getActiveByJudgementeeOutcome(target.getUuid(), outcome);

                boolean contains = false;
                for (Case caze : cases) {
                    if (caze.pipeline != null) {
                        if (caze.pipeline.toLowerCase().startsWith(punishments.get(index).getOffense().toLowerCase())) {
                            if (Integer.parseInt(caze.pipeline.split(",")[1]) == action.getOrder()) {
                                contains = true;
                            }
                            break;
                        }
                    }
                }

                // If the stage of the pipeline has already occurred continue to the next stage. If we are already on
                // the final stage then use that rather than dropping out of the loop without an outcome
                if (action.getOrder() == (punishments.get(index).getRepeat_actions().size() - 1)) contains = false;
                if (contains) continue;

                step = action.getOrder();
                break;
            }

            Punishment.RepeatedAction action = punishments.get(index).getRepeat_actions().get(step);
            Case newCase = new Case();
            newCase.assignee_id = u.getUuid();
            newCase.closed_at = System.currentTimeMillis();
            newCase.created_at = System.currentTimeMillis();
            newCase.created_by = u.getUuid();
            newCase.outcome = outcome;
            
            if (action.getDuration() != null)
                newCase.outcome_duration = action.getDuration() * action.getDuration_time_unit().getTime();

            newCase.pardoned = false;
            newCase.setRequires_review(!u.hasPermission("mod.no.review"));
            newCase.judgementee_id = target.getUuid();
            newCase.pipeline = punishments.get(index).getOffense().toLowerCase() + "," + step;
            newCase.reason = "Pipeline: " + punishments.get(index).getOffense().toLowerCase();
            newCase = newCase.saveAndReturn();

            // If the case kicks you then limbo the player if they are on the current server.
            // If they are not on the current server then send a punishment request out to the rest of the network.
            Player p = Bukkit.getPlayer(target.getDisplayName());
            if (newCase.getOutcome() != CaseOutcome.Muted && newCase.getOutcome() != CaseOutcome.IpMuted && newCase.getOutcome() != CaseOutcome.Warn) {
                if (p == null) {
                    PunishmentRequest r = new PunishmentRequest(targetName, "", u.getUuid(), newCase.id);
                    r.sendMessage("mslave.player." + targetName);

                    return;
                } else
                    limboPlayer(p, u, newCase.id);
            } else if (newCase.getOutcome() == CaseOutcome.Warn) {
                MessageAPI.sendMessage(null, u.getUuid(), "mod.cross.punishment.warn", true);
                MessageAPI.sendMessage(null, target.getUuid(), "mod.punishment.user.warn", true);
            }

            MessageAPI.sendMessage(null, u.getUuid(), "mod.cross.punishment.success", true);
        } catch (APIException e) {
            MessageAPI.sendMessage(null, u.getUuid(), "mod.cross.punishment.error", true);
            e.printStackTrace();
        }
    }

    public static void limboPlayer(Player target, User sender, UUID caseId) {
        PunishmentAPI.getPunishExecutions().forEach(a -> a.executeAction(target, null, null));

        Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> {
            Chunk startingChunk = target.getWorld().getChunkAt(target.getLocation().getBlockX(), target.getLocation().getBlockZ());
            int startX = startingChunk.getX() / 16;
            int startZ = startingChunk.getZ() / 16;

            // Lag
            int startY = 5;

            Schematic schematic = ModerationSlave.getSchematic();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(target)) continue;
                target.hidePlayer(p);
                p.hidePlayer(target);
            }
            target.setGameMode(GameMode.ADVENTURE);

            ModerationSlave.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(ModerationSlave.getInstance(), () -> {
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

                UUID currentCase = caseId;
                try {
                    User u = new User().getByUUID(target.getUniqueId());

                    List<Case> cases = new Case().getOpenByJudgementee(u.getUuid());
                    for (Case ca : cases) {
                        if (ca.isPardoned()) continue;
                        if (ca.getJudgement_session_id() != null) {
                            ModerationSlave.getInstance().getLogger().info("Judgement ID not null");
                            currentCase = ca.getId();
                            break;
                        }
                    }

                    if (currentCase == null) {
                        Case c = new Case();

                        if (sender != null) {
                            c.setAssignee_id(sender.getId());
                            c.setCreated_by(sender.getId());
                        }

                        c.setCreated_at(System.currentTimeMillis());
                        c.setJudgementee_id(u.getUuid());
                        c.setRequires_review(!u.hasPermission("mod.no.review"));

                        currentCase = c.saveAndReturn().getId();
                    }
                } catch (APIException e) {
                    e.printStackTrace();
                    MessageAPI.sendMessage(null, sender.getUuid(), "mod.cross.limbo.error", true);
                    return;
                }

                UUID finalCaseID = currentCase;
                Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> {
                    target.teleport(new Location(target.getWorld(),
                            startX * 16 - schematic.getOffset().getBlockX(),
                            startY - schematic.getOffset().getBlockY(),
                            startZ * 16 - schematic.getOffset().getBlockZ()));


                    if (sender != null)
                        MessageAPI.sendMessage(null, sender.getUuid(), "mod.cross.limbo.success", true);

                    target.kickPlayer("KickedForPunishment-" + finalCaseID);
                });
            }, 2L);
        });
    }
}
