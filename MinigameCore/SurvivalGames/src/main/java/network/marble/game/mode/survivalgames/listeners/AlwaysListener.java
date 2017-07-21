package network.marble.game.mode.survivalgames.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class AlwaysListener implements Listener {
	
	@EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeafDecay(BlockFadeEvent e) {
    	if(e.getBlock().getType() != Material.FIRE)
    		e.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        e.setCancelled(true);
    }

}
