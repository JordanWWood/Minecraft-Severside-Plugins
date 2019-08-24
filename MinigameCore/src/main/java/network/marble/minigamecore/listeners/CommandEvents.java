package network.marble.minigamecore.listeners;

import java.util.Iterator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.managers.CommandManager;

public class CommandEvents implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (CommandManager.disabledCommands.contains(e.getMessage())) e.setCancelled(true);
    }
    
    @EventHandler
    public void onTab(TabCompleteEvent e) {
        Iterator<String> cI = e.getCompletions().iterator();
        while(cI.hasNext()){
            String s = cI.next().toLowerCase().substring(1);
            //TODO IMPROVE
            if(s.contains(":") || s.startsWith("a") || s.startsWith("mgc") || s.startsWith("me") || s.startsWith("debug") ||  s.startsWith("he") || s.startsWith("i") || s.startsWith("k") || s.startsWith("p") || s.startsWith("tr") || s.startsWith("ve")){
                cI.remove();
            }
        }
    }
    
    @EventHandler
    public void onTab(PlayerCommandPreprocessEvent e) {
        String s = e.getMessage().split(" ")[0].toLowerCase().substring(1);
        MiniGameCore.logger.info(s);
        //TODO IMPROVE
        if(s.contains("?") || s.contains(":") || s.startsWith("a") || s.startsWith("mgc") || s.startsWith("me") || s.startsWith("he") || s.startsWith("i") || s.startsWith("k") || s.startsWith("pl") ||  s.startsWith("tr") || s.startsWith("ve")){
            e.setCancelled(true);
            e.getPlayer().sendMessage("Unknown command!");
        }
    }
}
