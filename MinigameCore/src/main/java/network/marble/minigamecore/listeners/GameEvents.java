package network.marble.minigamecore.listeners;

import network.marble.minigamecore.entities.messages.GameStatusUpdateMessage;
import network.marble.minigamecore.managers.AnalyticsManager;
import network.marble.minigamecore.managers.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.game.GameStatusChangeEvent;
import network.marble.minigamecore.entities.game.GameStatus;

import java.util.concurrent.TimeUnit;

public class GameEvents implements Listener {

    @EventHandler
    public void onGameStatusChange(GameStatusChangeEvent event) {
        if (event.oldStatus != event.newStatus) {
            GameStatusUpdateMessage message = new GameStatusUpdateMessage();
            message.status = event.newStatus;
            message.sendToServer();

            if (event.oldStatus == GameStatus.LOBBYING && event.newStatus == GameStatus.PRESTART) {
                MiniGameCore.teamManager.sortAllPlayersIntoTeams();
            } else if (event.oldStatus == GameStatus.FINISHED && event.newStatus == GameStatus.ENDED) {
                try {
                    AnalyticsManager.getInstance().saveBufferedAnalytics();
                } catch (Exception e) {
                    e.printStackTrace();
                    MiniGameCore.logger.severe("Failed to save user analytics because: "+e.getMessage());
                }
                TimerManager.getInstance().runIn((timer, last) -> {
                    MiniGameCore.logger.info("Server will now shutdown");
                    Bukkit.getServer().shutdown();
                }, 15, TimeUnit.SECONDS);
            }
        }
    }
}
