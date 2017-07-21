package network.marble.inventoryapi.interfaces;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.itemstacks.InventoryItem;

public interface ActionExecutor {
	
	/***
	 * Call for InventoryItems to execute specific actions on on being triggered.
	 * @param p The player that triggered this method
	 * @param s The InventoryItem clicked to trigger this method.
	 * @param args Any arguments that may be included along with the triggering of this action (may be null)
	 */
	public abstract void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String args[]);
}
