package network.marble.inventoryapi.enums;

public enum ArmorType {
	HELMET(0),
	CHEST(1),
	LEGS(2),
	BOOTS(3);
	
	private final int id;
	ArmorType(int id) {
		this.id = id;
	}
	public int getValue(){
		return id;
	}
}
