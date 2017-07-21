package network.marble.minigamecore.listeners;

import network.marble.minigamecore.managers.CommandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEvents implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (CommandManager.disabledCommands.contains(e.getMessage())) e.setCancelled(true);
    }
}
