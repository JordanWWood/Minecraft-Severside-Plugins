package network.marble.minigamecore.listeners;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.entities.team.Team;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.TeamManager;
import network.marble.moderationslave.events.ModerationChatEvent;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ModerationEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ModerationChatEvent event) {
        if (event.isCancelled()) return;
        event.setCancelled(true);

        MiniGamePlayer player = PlayerManager.getPlayer(event.getPlayer());
        String message = event.getMessage();
        ArrayList<MiniGamePlayer> players = new ArrayList<>();

        switch (player.playerType) {
            case ADMINISTRATOR:
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINISTRATOR));
                if (message.startsWith("!")) {
                    players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                    players.addAll(PlayerManager.getPlayers(PlayerType.PLAYER));
                    players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                }
                break;
            case MODERATOR:
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINISTRATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                if (message.startsWith("!")) {
                    players.addAll(PlayerManager.getPlayers(PlayerType.PLAYER));
                    players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                }
                break;
            case SPECTATOR:
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINISTRATOR));
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
                players.addAll(PlayerManager.getPlayers(PlayerType.ADMINISTRATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.MODERATOR));
                players.addAll(PlayerManager.getPlayers(PlayerType.SPECTATOR));
                break;
        }
        // TODO: Need to talk to jordan about added format into chat events
        //players.forEach(p -> p.getPlayer().sendMessage((TeamManager.isTeamChat() ? message.startsWith("!") ? "(G) " : "(T) " : "") + String.format(event.getFormat(), player.getPlayer().getDisplayName(), message.startsWith("!") ? message.substring(1) : message)));
        players.forEach(p -> p.getPlayer().sendMessage(message));
    }
}
