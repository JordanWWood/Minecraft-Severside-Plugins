package network.marble.inventoryapi.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ReducedMenuModeListener implements Listener{
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		UUID uuid = player.getUniqueId();
		/*Menu menu = InventoryAPIPlugin.playerCurrentMenus.get(uuid);
		if(menu instanceof PlayerListMenu){//TODO make a generic menu type that utilises the bottom of the inventory
			Inventory inv = InventoryAPIPlugin.inventories.get(InventoryAPIPlugin.playerInventories.get(uuid));
			player.getInventory().setContents(inv.getRealInventory());
			player.updateInventory();
		}*/
		
		InventoryAPIPlugin.playerCurrentMenus.remove(uuid);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemClick(InventoryClickEvent event) {
		if(InventoryAPIPlugin.playerCurrentMenus.get(event.getWhoClicked().getUniqueId()) == null) return;
		if(event.getInventory().getType() == InventoryType.CREATIVE) return;
		ItemStack is = event.getInventory().getType() == InventoryType.ANVIL ? event.getClickedInventory().getItem(0) : event.getCurrentItem();
		if(event.getSlot() >= 0){//Protect against clicks from outside of inventory window
			processClick((Player)event.getWhoClicked(), is, event.getSlot(), event.getRawSlot());
		}
		event.setCancelled(true);
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
	
	private void processClick(Player p, ItemStack itemStack, int slot, int rawSlot){
		if(p != null && itemStack != null){//Error catching and skipping empty item slots
			Menu m = InventoryAPIPlugin.playerCurrentMenus.get(p.getUniqueId());
			boolean dropThrough = true; //Determines whether to run normal item processing on a clicked slot.
			if(m != null){
				try{
					dropThrough = m.execute(slot, rawSlot);
				}catch(Exception e){
					e.printStackTrace();
					dropThrough = false;
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
