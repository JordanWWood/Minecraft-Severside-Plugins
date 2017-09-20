package network.marble.game.mode.survivalgames.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onFlint(BlockIgniteEvent e) {
    	if(e.getCause() == IgniteCause.FLINT_AND_STEEL){
    		short durability = (short) (e.getPlayer().getItemInHand().getDurability() + (short)20);
    		e.getPlayer().getItemInHand().setDurability(durability);
    	}
    }

	@EventHandler
    public void onSapling(ItemSpawnEvent e) {
    	if(e.getEntity().getItemStack().getType() == Material.SAPLING)
    		e.setCancelled(true);
    }
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onFireSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        e.setCancelled(true);
    }
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent e) {
    	if(e.getCause() == IgniteCause.SPREAD || e.getCause() == IgniteCause.FLINT_AND_STEEL && e.getBlock().getType() == Material.TNT) e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onMobSpawn(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL && e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.SOIL){
			e.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onHangingBreak(HangingBreakEvent event) {
		event.setCancelled(true);
	}
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onHangingPlace(HangingPlaceEvent event) {
		event.setCancelled(true);
	}
    
    @EventHandler(priority = EventPriority.NORMAL)
	public void onItemFrameInteraction(PlayerInteractEntityEvent event){
		if(event.getRightClicked() instanceof ItemFrame && event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}

}
