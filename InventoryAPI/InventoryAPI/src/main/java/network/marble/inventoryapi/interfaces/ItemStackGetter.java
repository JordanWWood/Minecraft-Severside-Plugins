package network.marble.inventoryapi.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.itemstacks.InventoryItem;

public interface ItemStackGetter {

	public abstract ItemStack getItemStack(InventoryItem inventoryItem, Player player);
	
}
