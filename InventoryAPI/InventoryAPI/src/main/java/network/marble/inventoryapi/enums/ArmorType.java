package network.marble.inventoryapi.enums;

public enum ArmorType {
	HELMET(3),
	CHEST(2),
	LEGS(1),
	BOOTS(0);
	
	private final int id;
	ArmorType(int id) {
		this.id = id;
	}
	public int getValue(){
		return id;
	}
}
