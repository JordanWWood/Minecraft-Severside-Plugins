package network.marble.inventoryapi.itemstacks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.interfaces.ItemStackGetter;

public class StandardItemStack extends InventoryItem{
	
	public StandardItemStack(ItemStack itemStack) {
		super(itemStack);
	}
	
	public StandardItemStack(ItemStack itemStack,ItemStackGetter getter) {
		super(itemStack, getter);
	}

	/**
	 * Does nothing as this is a display icon
	 */
	@Override
	public void execute(Player p, int slot) {
		//Do nothing
	}
}
