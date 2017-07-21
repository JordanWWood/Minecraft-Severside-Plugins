package network.marble.inventoryapi.inventories;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import network.marble.inventoryapi.enums.ArmorType;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ArmourSet {
	
	InventoryItem[] inventoryItems = new InventoryItem[4];
	@Getter
	ItemStack[] itemStacks = new ItemStack[4];
	
	public void setInventoryItem(ArmorType type, InventoryItem inventoryItem){
		inventoryItems[type.getValue()] = inventoryItem;
		itemStacks[type.getValue()] = inventoryItem.getItemStack(null);
	}
	
	public InventoryItem getArmorInventoryItem(ArmorType type){
		return inventoryItems[type.getValue()];
	}
}
