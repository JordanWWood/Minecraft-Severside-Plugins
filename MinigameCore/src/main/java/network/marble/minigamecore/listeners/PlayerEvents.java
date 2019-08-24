package network.marble.minigamecore.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.player.PlayerConnectEvent;
import network.marble.minigamecore.entities.events.player.PlayerDisconnectEvent;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.messages.PlayerLeaveMessage;
import network.marble.minigamecore.entities.messages.UnexpectedPlayerJoinMessage;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.AnalyticsManager;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerExpectationManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.scoreboards.Scoreboards;

public class PlayerEvents implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerType type = PlayerExpectationManager.getExpectedPlayerType(player.getUniqueId());
        MiniGameCore.logger.info(player.getName() + "-" + type);
        try {
			User u = new User().getByUUID(event.getPlayer().getUniqueId());
			if(type == null && u.hasPermission("moderation.goto")){
				PlayerExpectationManager.addPrePlayerRank(u.uuid, PlayerType.MODERATOR);
				type = PlayerExpectationManager.pullExpectedPlayerType(player.getUniqueId());
			}
			MiniGameCore.logger.info(player.getName() + "-" + u.hasPermission("moderation.goto"));
		} catch (APIException e) {
			e.printStackTrace();
		}
        MiniGameCore.logger.info(player.getName() + "-2" + type);
        if (type == null || (type == PlayerType.PLAYER && GameManager.getStatus() != GameStatus.LOBBYING)) {
            UnexpectedPlayerJoinMessage message = new UnexpectedPlayerJoinMessage();
            message.playerId = player.getUniqueId();
            message.sendToErrors();
            MiniGameCore.instance.getLogger().info("Unexpected 1");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Unexpected Player");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        PlayerType type = PlayerExpectationManager.pullExpectedPlayerType(player.getUniqueId());

        if (type == null) {
            try {
                User u = new User().getByUUID(event.getPlayer().getUniqueId());
                if (u.hasPermission("moderation.goto")) type = PlayerType.MODERATOR;
            } catch (APIException e) {
                e.printStackTrace();
            }
        }
        
        if (type != null) {
            MiniGamePlayer miniGamePlayer = PlayerManager.registerPlayer(player, type);
            PlayerManager.assignPlayerGameMode(player);
            MiniGameCore.logger.info("Player " + player.getDisplayName() + " joined with type " + type.toString());
            if (type == PlayerType.PLAYER)  {
                AnalyticsManager.getInstance().submitServerEvent(new network.marble.minigamecore.entities.analytics.server.PlayerJoinEvent(player));
                AnalyticsManager.getInstance().bufferAnalyticsForPlayer(miniGamePlayer.getUserId());
                Scoreboards.getScoreboardManager().installScoreboard(player.getUniqueId());
            }
            Bukkit.getServer().getPluginManager().callEvent(new PlayerConnectEvent(miniGamePlayer));

            MiniGameCore.instance.stopShutDownTimer();
//            player.sendMessage(ChatColor.GOLD + "Successfully joined a " + ChatColor.AQUA + ChatColor.BOLD
//            		+ GameManager.getCurrentMiniGame().getName() + ChatColor.GOLD  + " server!"); //TODO better customisation per game
        } else {
            MiniGameCore.instance.getLogger().info("Unexpected 2");
            player.kickPlayer("Unexpected Player");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogout(PlayerQuitEvent event)
    {
        event.setQuitMessage(null);
        playerLeave(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event)
    {
        event.setLeaveMessage(null);
        playerLeave(event.getPlayer(), true);
    }

    private void playerLeave(Player player, boolean kicked) {
        UUID uuid = player.getUniqueId();
        MiniGamePlayer mg = PlayerManager.getPlayer(uuid);
        if (mg == null) return;
        if (mg.playerType == PlayerType.PLAYER) {
            AnalyticsManager.getInstance().submitServerEvent(new network.marble.minigamecore.entities.analytics.server.PlayerLeaveEvent(player));
            if (GameManager.getStatus().ordinal() <= GameStatus.PRESTART.ordinal() ) {
                MiniGameCore.teamManager.removePlayerFromTeams(uuid, true);
                AnalyticsManager.getInstance().clearAnalyticsForPlayer(mg.getUserId());
            }
        }

        PlayerLeaveMessage message = new PlayerLeaveMessage(uuid, mg.playerType == PlayerType.PLAYER);
        message.sendToServer();
        PlayerManager.unregisterPlayer(uuid);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerDisconnectEvent(mg, player, kicked));
        MiniGameCore.instance.getLogger().info("PlayerLeave players online count: " + Bukkit.getOnlinePlayers().size());
        if (Bukkit.getOnlinePlayers().size() <= 0) MiniGameCore.instance.startShutDownTimer();
    }
}
