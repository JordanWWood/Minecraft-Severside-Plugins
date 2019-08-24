package network.marble.inventoryapi.enums;

public enum InventoryVisibility {
	GLOBAL(2), GROUP(1), PLAYER(0);
	
	public static final int groupCount = 3;
	int visibility;
	InventoryVisibility(int visibility){
		this.visibility = visibility;
	}
}
