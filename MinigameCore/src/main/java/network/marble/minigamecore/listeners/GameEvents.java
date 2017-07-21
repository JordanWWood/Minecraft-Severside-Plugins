package network.marble.minigamecore.listeners;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.events.game.GameStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameEvents implements Listener {

    @EventHandler
    public void onGameStatusChange(GameStatusChangeEvent event) {
        if (event.oldStatus == GameStatus.LOBBYING && event.newStatus == GameStatus.PRESTART) {
            MiniGameCore.teamManager.sortAllPlayersIntoTeams();
        }
    }
}
