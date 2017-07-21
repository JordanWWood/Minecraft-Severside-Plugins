package network.marble.minigamecore.listeners;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.messages.PlayerLeaveMessage;
import network.marble.minigamecore.entities.messages.UnexpectedPlayerJoinMessage;
import network.marble.minigamecore.entities.team.Team;
import network.marble.minigamecore.managers.*;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.scoreboards.Scoreboards;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.ArrayList;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerType type = PlayerExpectationManager.getExpectedPlayerType(player.getUniqueId());
        if (type == null) {
            UnexpectedPlayerJoinMessage message = new UnexpectedPlayerJoinMessage();
            message.playerId = player.getUniqueId();
            message.sendToErrors();
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Unexpected Player");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerType type = PlayerExpectationManager.pullExpectedPlayerType(player.getUniqueId());
        if (type != null) {
            PlayerManager.registerPlayer(player, type);
            PlayerManager.assignPlayerTraits(player);
            MiniGameCore.logger.info("Player " + player.getDisplayName() + " joined with rank " + type.toString());
            if (type == PlayerType.PLAYER) Scoreboards.getScoreboardManager().installScoreboard(player.getUniqueId());
        } else {
            player.kickPlayer("Unexpected Player");
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event)
    {
        playerLeave(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event)
    {
        playerLeave(event.getPlayer());
    }

    private void playerLeave(Player player) {
        MiniGamePlayer mg = PlayerManager.getPlayer(player);
        if (mg != null && mg.playerType == PlayerType.PLAYER) MiniGameCore.teamManager.removePlayerFromTeams(player.getUniqueId(), true);
        PlayerManager.unregisterPlayer(player);
        PlayerLeaveMessage message = new PlayerLeaveMessage(player.getUniqueId(), mg.playerType == PlayerType.PLAYER);
        message.sendToServer();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        MiniGamePlayer player = PlayerManager.getPlayer(event.getPlayer());
        String message = event.getMessage();
        ArrayList<MiniGamePlayer> players = new ArrayList<>();

        switch (player.playerType) {
            case ADMINSTRATOR:
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINSTRATOR));
                if (message.startsWith("!")) {
                    players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                    players.addAll(PlayerManager.getPlayers(PlayerType.PLAYER));
                    players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                }
                break;
            case MODERATOR:
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINSTRATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                if (message.startsWith("!")) {
                    players.addAll(PlayerManager.getPlayers(PlayerType.PLAYER));
                    players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                }
                break;
            case SPECTATOR:
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINSTRATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                break;
            case PLAYER:
                if (TeamManager.isTeamChat()) {
                    if (event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                        Team team = MiniGameCore.teamManager.getPlayersTeam(player.id);
                        if (team != null) players.addAll(team.getPlayers());
                    }
                } else {
                    players.addAll(PlayerManager.getPlayers(PlayerType.PLAYER));
                }
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINSTRATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                break;
        }
        players.forEach(p -> p.getPlayer().sendMessage((TeamManager.isTeamChat() ? message.startsWith("!") ? "(G) " : "(T) " : "") + String.format(event.getFormat(), player.getPlayer().getDisplayName(), message.startsWith("!") ? message.substring(1) : message)));
    }
}
