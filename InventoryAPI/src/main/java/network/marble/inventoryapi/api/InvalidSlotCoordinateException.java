package network.marble.inventoryapi.api;

public class InvalidSlotCoordinateException extends Exception{
	private static final long serialVersionUID = 8514694502385427790L;
	private static final String message = "The slot of the Inventory you attempted use was invalid.";
	public InvalidSlotCoordinateException(){
		super(message);
	}
	
	public InvalidSlotCoordinateException(int slot) {
        super(message + "The slot of the Inventory you attempted use was invalid. Value was " + slot + ".");
    }

	public InvalidSlotCoordinateException(int x, int y) {
		super(message + "The slot of the Inventory you attempted use was invalid. X was" + x + " and Y was" + y + ".");
	}
}
