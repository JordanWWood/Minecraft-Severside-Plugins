package network.marble.minigamecore.entities.task;

import network.marble.minigamecore.entities.events.tick.*;
import network.marble.minigamecore.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Duncan on 13/11/2016.
 */
public class CoreTickTask extends BukkitRunnable {
    public void run() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (GameManager.getCurrentMiniGame() != null) switch (GameManager.getStatus()) {
            case INITIALIZING:
                pm.callEvent(new InitalisingTickEvent());
                break;
            case LOBBYING:
                pm.callEvent(new LobbyTickEvent());
                break;
            case PRESTART:
                pm.callEvent(new PregameTickEvent());
                break;
            case INGAME:
                pm.callEvent(new IngameTickEvent());
                break;
            case FINISHED:
                pm.callEvent(new FinishedTickEvent());
                break;
            case ENDED:
                pm.callEvent(new EnddedTickEvent());
                break;
        }
    }
}