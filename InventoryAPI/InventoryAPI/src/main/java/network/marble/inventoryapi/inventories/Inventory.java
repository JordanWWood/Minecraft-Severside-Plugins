package network.marble.inventoryapi.inventories;

import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class Inventory{
	private InventoryItem[] items = new InventoryItem[36];
	private ItemStack[] realItemStacks = new ItemStack[36];
	private int inventoryID;
	
	public Inventory(int inventoryID){
		this.inventoryID = inventoryID;
	}
	
	public Inventory(int inventoryID, InventoryItem[] items, ItemStack[] realItemStacks){
		this.inventoryID = inventoryID;
		this.items = items;
		this.realItemStacks = realItemStacks;
	}
	
	/***
	 * Add item stacks to an inventory's main body.
	 * @param itemStack InventoryItem type input containing an itemStack
	 * @param xPos Slot position on x axis of main inventory section. This should be 1-9.
	 * @param yPos Slot position on y axis of main inventory section. This should be 1-4.
	 * @return Whether the input was successful
	 */
	public boolean addToInventory(InventoryItem inventoryItem, int xPos, int yPos){
		boolean success = false;
		
		int slot = InventoryAPI.calculatePlayerInventorySlot(xPos, yPos);
		if(slot >= 0){
			items[slot] = inventoryItem;
			realItemStacks[slot] = inventoryItem.getItemStack(null);
			success = true;
		}
		
		return success;
	}
	
	public ItemStack[] getRealInventory(){
		return realItemStacks;
	}
	
	public InventoryItem getInventoryItem(int slot){
		if(items.length > slot && slot >= 0) return items[slot];
		return null;
	}
	
	public InventoryItem[] getAllInventoryItems(){
		return items;
	}
	
	public int getInventoryID(){
		return inventoryID;
	}
}
