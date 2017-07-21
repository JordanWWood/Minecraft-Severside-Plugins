package network.marble.minigamecore.listeners;

import network.marble.minigamecore.entities.setting.WorldSettings;
import network.marble.minigamecore.managers.WorldManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockEvents  implements Listener {

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) e.setCancelled(worldSettings.isDisableLeafDecay());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) e.setCancelled(worldSettings.isDisableBlockBreaking());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) e.setCancelled(worldSettings.isDisableBlockPlacing());
    }
}