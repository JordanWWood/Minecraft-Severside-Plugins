package network.marble.inventoryapi.listeners;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

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
			processClick((Player)event.getWhoClicked(), is, event.getSlot());
		}
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeave(PlayerQuitEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		
		InventoryAPIPlugin.playerInventories.remove(uuid);
		InventoryAPIPlugin.playerCurrentMenus.remove(uuid);
		InventoryAPI.deletePlayerInventoryData(uuid);
	}
	
	private void processClick(Player p, ItemStack itemStack, int slot){
		if(p != null && itemStack != null){//Error catching and skipping empty item slots
			Menu m = InventoryAPIPlugin.playerCurrentMenus.get(p.getUniqueId());
			if(m != null) {
				try {
					m.execute(slot, slot);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
