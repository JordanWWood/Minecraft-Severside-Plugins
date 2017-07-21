package network.marble.inventoryapi.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.ArmourSet;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class MenuModeListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		InventoryAPIPlugin.playerArmour.put(event.getPlayer().getUniqueId(), new ArmourSet());
		InventoryAPIPlugin.playerInventories.put(event.getPlayer().getUniqueId(), 0);
		InventoryAPI.refreshPlayerView(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeave(PlayerQuitEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		
		InventoryAPIPlugin.playerInventories.remove(uuid);
		InventoryAPIPlugin.playerCurrentMenus.remove(uuid);
		if(InventoryAPIPlugin.getConfigModel().deleteDat){
			for(World w : Bukkit.getWorlds()){
				File f = new File(InventoryAPIPlugin.worldPlayersFolders.get(w.getName()), uuid.toString());
				if(f.exists()){
					f.delete();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameModeChange(PlayerGameModeChangeEvent event){
		if(event.getNewGameMode() == GameMode.CREATIVE){
			event.getPlayer().sendMessage(ChatColor.GOLD + "Menu mode has been " + org.bukkit.ChatColor.DARK_RED + "disabled" + ChatColor.GOLD + " for you as you have entered creative mode.");
		}
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE){
			event.getPlayer().getInventory().clear();
			InventoryAPI.refreshPlayerView(event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.GOLD + "Creative mode exited, inventory has been wiped and menu mode has been " + org.bukkit.ChatColor.GREEN + "enabled.");
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
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemUse(PlayerInteractEvent event) {
		if(event.getHand() == EquipmentSlot.OFF_HAND){//Prevent double running of this event due to secondary hand
			event.setCancelled(true);
			return;
		}
		Player p = event.getPlayer();
		if(p.getGameMode() != GameMode.CREATIVE){
			Action action = event.getAction();
			boolean cancel = true;
			if(action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK){
				switch(event.getClickedBlock().getType()){
					case ACACIA_DOOR:
					case BED_BLOCK:
					case BIRCH_DOOR:
					case BREWING_STAND:
					case BURNING_FURNACE:
					case CHEST:
					case DARK_OAK_DOOR:
					case ENCHANTMENT_TABLE:
					case FENCE_GATE:
					case FURNACE:
					case JUKEBOX:
					case JUNGLE_DOOR:
					case LEVER:
					case NOTE_BLOCK:
					case SHULKER_SHELL: 
					case SPRUCE_DOOR:
					case STONE_BUTTON:
					case TRAPPED_CHEST:
					case TRAP_DOOR:
					case TRIPWIRE:
					case WOOD_BUTTON:
					case WOODEN_DOOR:
					case WORKBENCH: cancel = false; break;
					default: break;
				}
			}
			
			if(p.getGameMode() == GameMode.ADVENTURE && action == Action.LEFT_CLICK_AIR) cancel = false; //Workaround for adventure mode messing with the interact event
			
			if(cancel && action != Action.PHYSICAL){
				event.setCancelled(cancel);
				PlayerInventory inv = p.getInventory();
				int slot = inv.getHeldItemSlot();
				processClick(p, inv.getItem(slot), slot, inv.getHeldItemSlot());
			}
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
	public void onSwapHand(PlayerSwapHandItemsEvent event){
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
	
	private void processClick(Player p, ItemStack itemStack, int slot, int rawSlot){
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
			
			if(dropThrough){//TODO add player specific level slot checking first
				if(InventoryAPIPlugin.playerInventories.containsKey(p.getUniqueId())){
					Integer invID = InventoryAPIPlugin.playerInventories.get(p.getUniqueId());
					if(InventoryAPIPlugin.inventories.containsKey(invID)){
						InventoryItem item = (slot == 40 ? InventoryAPI.getOffHandItem(p) : InventoryAPIPlugin.inventories.get(invID).getInventoryItem(slot));
						if(item != null){//Check group level inventory first
							try{
								item.execute(p, slot);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else if(InventoryAPIPlugin.globalItems.containsKey(slot)){
							try{
								InventoryAPIPlugin.globalItems.get(slot).execute(p, slot);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}
