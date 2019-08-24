package network.marble.moderation.listeners;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GlobalVariable;
import network.marble.dataaccesslayer.models.plugins.moderation.*;
import network.marble.dataaccesslayer.models.user.ForumData;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.hermes.utils.ModerationCache;
import network.marble.hermes.utils.ModerationPlayer;
import network.marble.moderation.Moderation;
import network.marble.moderation.messages.ChatMessage;
import network.marble.moderation.punishment.PunishmentManager;
import network.marble.moderation.punishment.communication.RabbitManager;
import network.marble.moderation.punishment.net.PunishmentBridge;
import network.marble.moderation.utils.FontFormat;

public class PlayerListener implements Listener {
    private User createUser(PendingConnection p) throws APIException {//TODO document that changes to the user affect this method
        System.out.println("called");
        User user = new User();
        user.setBadgesInProgress(new ArrayList<>());
        user.setEarnedBadges(new HashMap<>());
        user.setBalances(new HashMap<>());
        user.setBlockedUsers(new ArrayList<>());
        user.setDisplayName(p.getName());
        user.setForumdata(new ForumData());
        user.setIp(p.getAddress().getAddress().toString().substring(1));
        user.setModeration(new network.marble.dataaccesslayer.models.user.Moderation());
        user.setName(p.getName().toLowerCase());
        user.setPreferences(new HashMap<>());
        user.setFirstJoin(System.currentTimeMillis());
        user.setRank_id(UUID.fromString(new GlobalVariable().getByName("defaultRank").value));
        user.setUuid(p.getUniqueId());
        user.setVanityitems(new HashMap<>());

        user.insert();
        return user;
    }

    private User updateUser(User user, PendingConnection p) throws APIException {//TODO document that changes to the user affect this method
        if (!user.getIp().equals(p.getAddress().toString()))
            user.setIp(p.getAddress().getAddress().toString().substring(1));
        if (!user.getDisplayName().equals(p.getName())) user.setDisplayName(p.getName());
        if (!user.getName().equals(p.getName().toLowerCase())) user.setName(p.getName().toLowerCase());

        Moderation.getInstance().getLogger().info(user.uuid.toString());
        user.update();
        return user;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerConnect(LoginEvent e) {
        e.registerIntent(Moderation.getInstance());

        ProxyServer.getInstance().getScheduler().runAsync(Moderation.getInstance(), () -> {
            PendingConnection player = e.getConnection();
            User user = null;
            List<Case> temp = null;
            List<Case> perm = null;

            List<ActivePunishment> activePunishments = new ArrayList<>();

            try {
                user = new User().get(player.getUniqueId());
                if (!user.exists()) user = createUser(player);
                else user = updateUser(user, player);

                Rank r = new Rank().get(user.getRank_id());

                ModerationCache.cache.putIfAbsent(user.getUuid(), new ModerationPlayer(user, r));

                if (user.rank_id == null || !user.hasPermission("moderation.join")) {
                    e.setCancelled(true);
                    player.disconnect(FontFormat.translateString("&cServer is currently down for maintenance"));

                    return;
                }

                temp = new Case().getActiveByJudgementeeOutcome(user.getUuid(), CaseOutcome.TemporaryBan);
                perm = new Case().getActiveByJudgementeeOutcome(user.getUuid(), CaseOutcome.PermanentlyBan);

                if (temp != null && !temp.isEmpty())
                    for (Case c : temp)
                        Moderation.getInstance().getLogger().info(c.getOutcome() + "");
                else
                    Moderation.getInstance().getLogger().info("Null or empty 1");

                if (perm != null && !perm.isEmpty())
                    for (Case c : perm)
                        Moderation.getInstance().getLogger().info(c.getOutcome() + "");
                else
                    Moderation.getInstance().getLogger().info("Null or empty 2");

                long now = System.currentTimeMillis();
                boolean isPerm = false;
                long largest = 0L;

                if (perm == null || perm.isEmpty()) {
                    if (temp != null && !temp.isEmpty())
                        for (Case c : temp)
                            if ((c.getClosed_at() + c.getOutcome_duration()) > largest && !c.isPardoned() && c.getClosed_at() + c.getOutcome_duration() > System.currentTimeMillis())
                                largest = c.getClosed_at() + c.getOutcome_duration();
                } else
                    for (Case c : perm)
                        if (c.getOutcome().equals(CaseOutcome.PermanentlyBan) && !c.isPardoned())
                            isPerm = true;

                if (isPerm) {
                    e.setCancelled(true);
                    e.setCancelReason(new TextComponent(FontFormat.translateString("&cYou been &epermanently banned &cfrom Server.\n &cAppeal at: &fhttps://Server.net/appeals")));

                    e.completeIntent(Moderation.getInstance());
                    return;
                }

                if (largest > 0) {
                    long diff = largest - now;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(largest);

                    calendar.add(Calendar.MONTH, 1);

                    e.setCancelled(true);
                    String reason = String.format(FontFormat.translateString("&cYou have been &ebanned &cfrom Server.\n &cYou will be able to rejoin on %d-%d-%d.\n &cAppeal at: &fhttps://Server.net/appeals"),
                            calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.YEAR));
                    e.setCancelReason(new TextComponent(reason));
                }
            } catch (Exception e1) {
                e.setCancelled(true);
                e1.printStackTrace();
                player.disconnect(new TextComponent("An error occurred. If this keeps happening please let a member of staff know! Reason: " + e1.getMessage()));
            }

            e.completeIntent(Moderation.getInstance());
        });
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        if (Moderation.getPunishmentManager().isUnderPunishment(e.getPlayer().getUniqueId()))
            RabbitManager.unBindKeyForUser(e.getPlayer().getUniqueId());

        ModerationCache.cache.remove(e.getPlayer().getUniqueId());

        antiSpam.remove(e.getPlayer().getUniqueId());
        spamCount.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        // We need to override the Downstream class of each user so that we can override the disconnect methods of it.
        // ServerSwitchEvent is called just right after the Downstream Bridge has been initialized, so we simply can
        // instantiate here our own implementation of the DownstreamBridge.
        // Also this is called when players join for some reason
        //
        // @see net.md_5.bungee.ServerConnector#L249

        ProxyServer bungee = Moderation.getInstance().getProxy();
        UserConnection user = (UserConnection) e.getPlayer();
        ServerConnection server = user.getServer();
        ChannelWrapper ch = server.getCh();

        PunishmentBridge bridge = new PunishmentBridge(bungee, user, server);
        ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(bridge);
    }

    private Map<UUID, Long> antiSpam = new HashMap<>();
    private Map<UUID, Integer> spamCount = new HashMap<>();
    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        ProxiedPlayer player = ((ProxiedPlayer) e.getSender());
        if (!antiSpam.containsKey(player.getUniqueId()))
            antiSpam.put(player.getUniqueId(), System.currentTimeMillis());
        else {
            if (antiSpam.get(player.getUniqueId()) + 1500 > System.currentTimeMillis()) {
                e.setCancelled(true);

                antiSpam.replace(player.getUniqueId(), System.currentTimeMillis());

                if (!spamCount.containsKey(player.getUniqueId())) spamCount.put(player.getUniqueId(), 1);
                else spamCount.replace(player.getUniqueId(), spamCount.get(player.getUniqueId()) + 1);

                if (spamCount.get(player.getUniqueId()) >= 5) {
                    e.getSender().disconnect("Spam protection");
                    return;
                }
                player.sendMessage(new TextComponent("Slow down your message rate!"));
            } else {
                spamCount.remove(player.getUniqueId());
                antiSpam.remove(player.getUniqueId());
            }
        }

        if (!Moderation.getPunishmentManager().isUnderPunishment(((ProxiedPlayer) e.getSender()).getUniqueId())) return;

        ((ProxiedPlayer) e.getSender()).sendMessage(((ProxiedPlayer) e.getSender()).getDisplayName() + ": " + e.getMessage());

        ChatMessage cm = new ChatMessage();
        cm.setMessage(e.getMessage());
        cm.setUuid(((ProxiedPlayer) e.getSender()).getUniqueId());
        cm.sendMessage(150, "api.moderation");
    }
}
