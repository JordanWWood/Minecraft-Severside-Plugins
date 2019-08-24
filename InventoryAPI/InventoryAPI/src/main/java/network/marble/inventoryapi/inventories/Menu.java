package network.marble.inventoryapi.inventories;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.itemstacks.InventoryItem;

public abstract class Menu {
	private Player targetPlayer;
	private InventoryItem inventoryItem;
	private int inventorySize;
	
	public Menu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize){
		this.targetPlayer = targetPlayer;
		this.inventoryItem = inventoryItem;
		this.inventorySize = inventorySize;
	}
	
	/***
	 * Standard execute methods for menus to run from MenuMode calls
	 * @param slot
	 * @param rawSlot
	 * @return Whether to execute a click outside of the menus processor.
	 */
	public abstract boolean execute(int slot, int rawSlot);
	
	public Player getTargetPlayer(){
		return targetPlayer;
	}
	
	public InventoryItem getInventoryItem(){
		return inventoryItem;
	}
	
	public int getInventorySize(){
		return inventorySize;
	}
}
