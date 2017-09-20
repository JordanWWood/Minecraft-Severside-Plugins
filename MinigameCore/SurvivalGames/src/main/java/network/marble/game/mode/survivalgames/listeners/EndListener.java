package network.marble.game.mode.survivalgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EndListener implements Listener {
	@EventHandler
    public void onDamage(EntityDamageEvent e) {
    	e.setCancelled(true);
    }
}
