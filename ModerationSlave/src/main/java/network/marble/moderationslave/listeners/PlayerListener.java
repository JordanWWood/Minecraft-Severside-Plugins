package network.marble.moderationslave.listeners;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import lombok.Setter;
import network.marble.dataaccesslayer.models.plugins.moderation.*;
import network.marble.messageapi.api.MessageAPI;
import network.marble.moderationslave.communication.RabbitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messagelibrary.api.Lang;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.commands.PunishmentCommand;
import network.marble.moderationslave.events.ModerationChatEvent;
import network.marble.moderationslave.utils.FontFormat;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private HashMap<UUID, LinkedList<String>> messageLog = new HashMap<>();
    private HashMap<UUID, Long> lastPlayerMessageTime = new HashMap<>();

    @Setter
    private static boolean isSlowmode = false;
    @Setter
    private static int slowmodeSpeed = 5;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM, yyyy");


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onModerationChat(ModerationChatEvent e) {
        if (!e.isCancelled()) {
            try {
                User u = new User().getByUUID(e.getPlayer().getUniqueId());

                Rank r = new Rank().get(u.getRank_id());
                String message = e.getMessage();

                ArrayList<BaseComponent> components = new ArrayList<>();
                components.add(new TextComponent(FontFormat.translateString("Rank: " + r.prefix + r.name)));
                components.add(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
                components.add(
                        new TextComponent(
                        FontFormat.translateString(
                        dateFormat.format(
                        new Timestamp(u.getFirstJoin())))));
                BaseComponent[] hoverToSend = (BaseComponent[]) components.toArray(new BaseComponent[components.size()]);

                TextComponent parentObject = new TextComponent("");
                TextComponent prefixTC = new TextComponent(FontFormat.translateString((e.getPrefix() != null ? e.getPrefix() : r.getPrefix())
                        + u.getDisplayName() + (e.getSuffix() != null ? e.getSuffix() : r.getSuffix()) + ":"));

                prefixTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverToSend));
                parentObject.addExtra(prefixTC);

                if (!u.hasPermission("mod.chat.colour"))
                    parentObject.addExtra(new TextComponent(TextComponent.fromLegacyText(FontFormat.translateString(r.getSuffix()) + " " + message)));
                else
                    parentObject.addExtra(new TextComponent(TextComponent.fromLegacyText(FontFormat.translateString(r.getSuffix() + " " + message))));

                for (Player p : e.getRecepients())
                    p.spigot().sendMessage(parentObject);
            } catch (APIException e1) {
                e1.printStackTrace();
            }//TODO swap when sideloading works
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        try {
            String address = p.getAddress().getAddress().toString();
            List<ActivePunishment> punishments = new ActivePunishment().getByIpAndAction(address.substring(1, address.length()), ActivePunishmentActionType.IpMuted);

            if (!punishments.isEmpty()) {
                for (ActivePunishment punishment : punishments)
                    if (punishment.getExpiryDate() > System.currentTimeMillis()) {
                        MessageAPI.sendMessage(null, p.getUniqueId(), "mod.chat.muted", true);

                        e.setCancelled(true);
                        return;
                    }
            }

            List<Case> cases = new Case().getActiveByJudgementeeOutcome(p.getUniqueId(), CaseOutcome.Muted);
            if (cases != null && !cases.isEmpty())
                for (Case currentCase : cases) {
                    ModerationSlave.getInstance().getLogger().info((currentCase.getCreated_at() + currentCase.getOutcome_duration()) + "");

                    if (!currentCase.isPardoned() && (currentCase.getClosed_at() + currentCase.getOutcome_duration()) > System.currentTimeMillis()) {
                        MessageAPI.sendMessage(null, p.getUniqueId(), "mod.chat.muted", true);

                        e.setCancelled(true);
                        return;
                    }
                }
        } catch (APIException e1) {
            e1.printStackTrace();
        }

        if (isSlowmode) {
            if (lastPlayerMessageTime.containsKey(e.getPlayer().getUniqueId())) {
                if (lastPlayerMessageTime.get(e.getPlayer().getUniqueId()) + slowmodeSpeed * 1000 > System.currentTimeMillis()) {
                    p.sendMessage(Lang.get("moderation.slowmode.cancel", p));

                    e.setCancelled(true);
                    return;
                } else {
                    lastPlayerMessageTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                }
            } else {
                lastPlayerMessageTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
            }
        }

        // Message log
        if (!messageLog.containsKey(p.getUniqueId())) messageLog.put(p.getUniqueId(), new LinkedList<>());

        LinkedList<String> messages = messageLog.get(p.getUniqueId());
        if (messages.size() >= 10) messages.removeFirst();

        messages.add(e.getMessage());
        messageLog.replace(p.getUniqueId(), messages);

        // Chat Filter
        if (Lang.shouldFilter(e.getMessage())) {
            e.setCancelled(true);
            p.sendMessage(Lang.get("mod.chat.filtered", p));
            return;
        }

        Bukkit.getServer().getPluginManager().callEvent(new ModerationChatEvent(new ArrayList<Player>(Bukkit.getOnlinePlayers()), e.getPlayer(), e.getMessage()));
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(ModerationSlave.getInstance(), () -> {
            Bukkit.getScheduler().runTaskAsynchronously(ModerationSlave.getInstance(), () -> {
                RabbitManager.bindKeyForUser(p.getName());

                User u = null;
                List<Case> cases = null;
                try {
                    u = new User().get(e.getPlayer().getUniqueId());
                    cases = new Case().getOpenByJudgementee(u.getUuid());
                } catch (APIException ex) {
                    ex.printStackTrace();
                    return;
                }

                for (Case ca : cases) {
                    if (ca.isPardoned()) continue;
                    if (ca.getJudgement_session_id() != null) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ModerationSlave.getInstance(), () -> {
                            PunishmentCommand.limboPlayer(e.getPlayer(), null, null);
                        });

                        return;
                    }
                }
                try {
                    Rank r = new Rank().get(u.getRank_id());

                    if (r.getPrefix() == null || p.getUniqueId() == null) return;
//                    PlayerManager.getInstance().setPlayerNameColour(p.getUniqueId(), FontFormat.translateString(r.getPrefix()));
//                    //p.setPlayerListName(FontFormat.translateString(r.getPrefix() /*+ " &7"*/ + p.getName()));
//                    PlayerManager.getInstance().installAllColoursForPlayer(u.getUuid());
                } catch (APIException e1) {
                    e1.printStackTrace();
                }

//                for (Player target : Bukkit.getOnlinePlayers()) {
//                    if (target.getUniqueId().equals(p.getUniqueId())) continue;
//
//                    try {
//                        u = new User().getByUUID(target.getUniqueId());
//                        Rank r = new Rank().get(u.getRank_id());
//
//                        target.setPlayerListName(FontFormat.translateString(r.getPrefix() + " &7" + target.getName()));
//                    } catch (APIException e1) {
//                        e1.printStackTrace();
//                    }
//
//                }
            });
        }, 10);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        RabbitManager.unBindKeyForUser(e.getPlayer().getName());

        messageLog.remove(e.getPlayer().getUniqueId());
        lastPlayerMessageTime.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/.") && !e.getMessage().startsWith("/. "))
            e.setMessage(e.getMessage().replace("/.", "/. "));
    }
}






















