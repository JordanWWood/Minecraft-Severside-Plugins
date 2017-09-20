package network.marble.minigamecore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import network.marble.minigamecore.entities.setting.WorldSettings;
import network.marble.minigamecore.managers.WorldManager;

public class EntityEvents implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        String worldName = e.getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null && worldSettings.isDisableBlockExplosionDamage()) e.blockList().clear();
    }
}