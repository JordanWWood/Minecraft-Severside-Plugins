package network.marble.inventoryapi.api;

import network.marble.inventoryapi.enums.InventoryVisibility;

public class SlotCollisionException extends Exception{
	private static final long serialVersionUID = 8514694502385427790L;
	private static final String message = "The slot of the Inventory you attempted to insert an InventoryItem into already contains an InventoryItem.";
	public SlotCollisionException(){
		super(message);
	}
	
	public SlotCollisionException(int slot) {
        super(message + "This occurred while attempting to insert an item into slot " + slot + ".");
    }
	
	public SlotCollisionException(int slot, InventoryVisibility visibility) {
        super(message + "This occurred while attempting to insert an item into slot " + slot + " of an inventory with visibility level " + visibility.toString() + ".");
    }
}
