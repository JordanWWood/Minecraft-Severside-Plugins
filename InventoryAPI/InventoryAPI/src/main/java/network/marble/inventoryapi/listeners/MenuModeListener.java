package network.marble.inventoryapi.listeners;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.ArmourSet;
import network.marble.inventoryapi.inventories.Menu;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class MenuModeListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		InventoryAPIPlugin.playerArmour.put(event.getPlayer().getUniqueId(), new ArmourSet());
		InventoryAPIPlugin.playerInventories.put(event.getPlayer().getUniqueId(), 0);
		InventoryAPI.refreshPlayerView(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave(PlayerQuitEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		
		InventoryAPIPlugin.playerInventories.remove(uuid);
		InventoryAPIPlugin.playerCurrentMenus.remove(uuid);
		InventoryAPI.deletePlayerInventoryData(uuid);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameModeChange(PlayerGameModeChangeEvent event){
		if(event.getNewGameMode() == GameMode.CREATIVE){
			event.getPlayer().sendMessage(ChatColor.GOLD + "Menu mode has been " + ChatColor.DARK_RED + "disabled" + ChatColor.GOLD + " for you as you have entered creative mode.");
		}
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE){
			event.getPlayer().getInventory().clear();
			InventoryAPI.refreshPlayerView(event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.GOLD + "Creative mode exited, inventory has been wiped and menu mode has been " + ChatColor.GREEN + "enabled.");
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		InventoryAPIPlugin.playerCurrentMenus.remove(uuid);
	}
		
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemClick(InventoryClickEvent event) {
		ItemStack is = event.getInventory().getType() == InventoryType.ANVIL ? event.getClickedInventory().getItem(0) : event.getCurrentItem();
		if(event.getWhoClicked().getGameMode() != GameMode.CREATIVE){
			if(event.getSlot() >= 0){//Protect against clicks from outside of inventory window
				processClick((Player)event.getWhoClicked(), is, event.getSlot(), event.getRawSlot());
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onHitBlockInAdventureMode(PlayerAnimationEvent event) {//Fixes the issue of clients not sending an update for swinging at a block in adventure mode
		Player p = event.getPlayer();
        if(event.getAnimationType() == PlayerAnimationType.ARM_SWING && p.getGameMode() == GameMode.ADVENTURE) {
            PlayerInventory inv = p.getInventory();
			int slot = inv.getHeldItemSlot();
			processClick(p, inv.getItem(slot), slot, inv.getHeldItemSlot());
        }
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemFrameInteraction(PlayerInteractEntityEvent event){
		if(event.getRightClicked() instanceof ItemFrame && event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemPickup(PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemUse(InventoryPickupItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER)){
			event.setCancelled(true);
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
	public void onPlayerShearEntity(PlayerShearEntityEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSheepDyeWool(SheepDyeWoolEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleDestroy(VehicleDestroyEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTame(EntityTameEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityShootBow(EntityShootBowEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPortalCreate(PortalCreateEvent event){
		event.setCancelled(true);
	}
	
	public static void processClick(Player p, ItemStack itemStack, int slot, int rawSlot){
		if(p != null && itemStack != null){//Error catching and skipping empty item slots
			Menu m = InventoryAPIPlugin.playerCurrentMenus.get(p.getUniqueId());
			boolean dropThrough = true; //Determines whether to run normal item processing on a clicked slot.
			if(m != null){
				try{
					dropThrough = m.execute(slot, rawSlot);
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
			}else{//Prevent an open chest messing with clicks
				if(p.getOpenInventory().getType() != InventoryType.CRAFTING){
					return;//Non-menu inventories must be ignored
				}
			}

			//TODO add player specific level slot checking first
            if(dropThrough) InventoryAPI.clickProcess(p, slot);
		}
	}
}
