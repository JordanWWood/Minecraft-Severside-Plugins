package network.marble.moderation.listeners;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GlobalVariable;
import network.marble.dataaccesslayer.models.user.ForumData;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderation.Moderation;
import network.marble.moderation.punishment.net.PunishmentBridge;
import network.marble.moderation.utils.ModerationPlayer;

public class PlayerListener implements Listener {
    public static ConcurrentMap<UUID, ModerationPlayer> cache = new ConcurrentHashMap<>();
    
    private User createUser(PendingConnection p) throws APIException{//TODO document that changes to the user affect this method
    	System.out.println("called");
    	User user = new User();
		user.setBadges(new ArrayList<>());
		user.setBalances(new HashMap<>());
		user.setBlockedUsers(new ArrayList<>());
		user.setDisplayName(p.getName());
		user.setForumdata(new ForumData());
		user.setIp(p.getAddress().getAddress().toString().substring(1));
		user.setModeration(new network.marble.dataaccesslayer.models.user.Moderation());
		user.setName(p.getName().toLowerCase());
		user.setPreferences(new HashMap<>());
		user.setRank_id(UUID.fromString(new GlobalVariable().getByName("defaultRank").value));
		user.setUuid(p.getUniqueId());
		user.setVanityitems(new HashMap<>());


		user.save();
		return user;
    }

    private User updateUser(User user, PendingConnection p) throws APIException{//TODO document that changes to the user affect this method
        if (!user.getIp().equals(p.getAddress().toString())) user.setIp(p.getAddress().getAddress().toString().substring(1));
        if (!user.getDisplayName().equals(p.getName())) user.setDisplayName(p.getName());
        if (!user.getName().equals(p.getName().toLowerCase())) user.setName(p.getName().toLowerCase());

        user.save();
        return user;
    }


    @EventHandler
    public void onPlayerConnect(LoginEvent e) {
    	PendingConnection player = e.getConnection();
        new Thread(() -> {
            User user = null;
            try {
                user = new User().getByUUID(player.getUniqueId());
                if(!user.exists()){
                	user = createUser(e.getConnection());
                } else {
                    user = updateUser(user, e.getConnection());
                }
            } catch (APIException e1) {
                e1.printStackTrace();
                Thread.currentThread().interrupt();
            }

            long now = System.currentTimeMillis();
            if (user.getModeration().banned) {
                if (user.getModeration().ban_end_time > now) {
                    long diff = user.getModeration().ban_end_time - now;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(user.getModeration().ban_end_time);

                    long days = TimeUnit.MILLISECONDS.toDays(diff);
                    diff -= TimeUnit.DAYS.toMillis(days);
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    diff -= TimeUnit.HOURS.toMillis(hours);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);



                    e.setCancelled(true);
                    e.setCancelReason(String.format("You have been banned! You will be able to rejoin on the %d-%d-%d. This is in %d day(s), %d hours and %d minutes",
                            calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.YEAR), days, hours, minutes));
                } else {
                    user.getModeration().banned = false;
                    user.getModeration().ban_end_time = 0L;
                    try {
                        user.update();
                    } catch (APIException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        // We need to override the Downstream class of each user so that we can override the disconnect methods of it.
        // ServerSwitchEvent is called just right after the Downstream Bridge has been initialized, so we simply can
        // instantiate here our own implementation of the DownstreamBridge.
        // Also this is called when players join for some reason
        //
        // @see net.md_5.bungee.ServerConnector#L249

        Moderation.getInstance().getLogger().info(e.getPlayer().getDisplayName() + " has switched server");

        ProxyServer bungee = Moderation.getInstance().getProxy();
        UserConnection user = (UserConnection) e.getPlayer();
        ServerConnection server = user.getServer();
        ChannelWrapper ch = server.getCh();

        PunishmentBridge bridge = new PunishmentBridge(bungee, user, server);
        ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(bridge);
    }

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        //TODO Message cache
        if (!Moderation.getPunishmentManager().isUnderPunishment(((ProxiedPlayer) e.getSender()).getUniqueId())) return;

        ((ProxiedPlayer) e.getSender()).sendMessage(((ProxiedPlayer) e.getSender()).getDisplayName() + ": " + e.getMessage());
        Moderation.getInstance().getLogger().info("Message sent by player in judgement: " + e.getMessage());
        //TODO send via rabbit to panel
    }
}
