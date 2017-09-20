package network.marble.minigamecore.listeners;

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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerType type = PlayerExpectationManager.getExpectedPlayerType(player.getUniqueId());
        try {
			User u = new User().getByUUID(event.getPlayer().getUniqueId());
			if(type == null && u.hasPermission("moderation.goto")){
				PlayerExpectationManager.addPrePlayerRank(u.uuid, PlayerType.MODERATOR);
				type = PlayerExpectationManager.pullExpectedPlayerType(player.getUniqueId());
			}
		} catch (APIException e) {
			e.printStackTrace();
		}
        
        if (type == null || (type == PlayerType.PLAYER && GameManager.getStatus() != GameStatus.LOBBYING)) {
            UnexpectedPlayerJoinMessage message = new UnexpectedPlayerJoinMessage();
            message.playerId = player.getUniqueId();
            message.sendToErrors();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Unexpected Player");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerType type = PlayerExpectationManager.pullExpectedPlayerType(player.getUniqueId());
        if (type != null) {
            event.setJoinMessage(null);
            MiniGamePlayer miniGamePlayer = PlayerManager.registerPlayer(player, type);
            PlayerManager.assignPlayerGameMode(player);
            MiniGameCore.logger.info("Player " + player.getDisplayName() + " joined with type " + type.toString());
            if (type == PlayerType.PLAYER)  {
                AnalyticsManager.getInstance().bufferAnalyticsForPlayer(miniGamePlayer.getUserId());
                Scoreboards.getScoreboardManager().installScoreboard(player.getUniqueId());
            }
            Bukkit.getServer().getPluginManager().callEvent(new PlayerConnectEvent(miniGamePlayer));

            MiniGameCore.instance.stopShutDownTimer();

        } else {
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
        if (mg.playerType == PlayerType.PLAYER && GameManager.getStatus().ordinal() <= GameStatus.PRESTART.ordinal() ) {
            MiniGameCore.teamManager.removePlayerFromTeams(uuid, true);
            AnalyticsManager.getInstance().clearAnalyticsForPlayer(mg.getUserId());
        }

        PlayerLeaveMessage message = new PlayerLeaveMessage(uuid, mg.playerType == PlayerType.PLAYER);
        message.sendToServer();
        PlayerManager.unregisterPlayer(uuid);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerDisconnectEvent(mg, player, kicked));

        if (Bukkit.getOnlinePlayers().size() <= 0) MiniGameCore.instance.startShutDownTimer();
    }
}
