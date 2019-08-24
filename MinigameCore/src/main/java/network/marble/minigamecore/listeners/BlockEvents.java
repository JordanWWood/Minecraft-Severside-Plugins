package network.marble.minigamecore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import network.marble.minigamecore.entities.setting.WorldSettings;
import network.marble.minigamecore.managers.WorldManager;

public class BlockEvents implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) {
            e.getBlock().setData((byte)(e.getBlock().getData()+4));
            e.setCancelled(worldSettings.isDisableLeafDecay());
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) {
            e.setCancelled(worldSettings.isDisableBlockFade());
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) {
            e.setCancelled(worldSettings.isDisableBlockSpread());
        }
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

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null) e.setCancelled(worldSettings.isDisableBlockIgnite());
        if (!e.isCancelled() && e.getCause() == BlockIgniteEvent.IgniteCause.SPREAD && worldSettings != null) e.setCancelled(worldSettings.isDisableFireSpread());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        String worldName = e.getBlock().getLocation().getWorld().getName();
        WorldSettings worldSettings = WorldManager.getCurrentWorldsSettings().get(worldName);
        if (worldSettings != null && worldSettings.isDisableBlockExplosionDamage()) e.blockList().clear();
    }
}