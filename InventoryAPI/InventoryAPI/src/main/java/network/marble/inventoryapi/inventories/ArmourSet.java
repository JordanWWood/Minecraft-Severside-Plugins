package network.marble.inventoryapi.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.enums.ArmorType;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ArmourSet {
	protected InventoryItem[] inventoryItems = new InventoryItem[4];
	
	public void setInventoryItem(ArmorType type, InventoryItem inventoryItem){
		inventoryItems[type.getValue()] = inventoryItem;
	}
	
	public InventoryItem getArmorInventoryItem(ArmorType type){
		return inventoryItems[type.getValue()];
	}
	
	public ItemStack[] getItemStacks(){
		return getItemStacks(null);
	}
	
	public ItemStack[] getItemStacks(Player p){
		ItemStack[] itemStacks = new ItemStack[4];
		for(int i = 0; i < 4; i++){
			itemStacks[i] = inventoryItems[i]!=null ? inventoryItems[i].getItemStack(p) : new ItemStack(Material.AIR);
		}
		return itemStacks;
	}
}
