package network.marble.inventoryapi.itemstacks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import network.marble.inventoryapi.interfaces.ItemStackGetter;

public abstract class InventoryItem {
	private ItemStack itemStack;
	@Getter
	private ItemStackGetter getter = null;
	@Getter
	private boolean usesGetter = false;
	
	public InventoryItem(ItemStack itemStack){
		this.itemStack = itemStack;
	}
	
	public InventoryItem(ItemStack itemStack, ItemStackGetter getter){
		this.itemStack = itemStack;
		this.getter = getter;
		if(getter != null) this.usesGetter = true;
	}
	
	/***
	 * Get the correct ItemStack representation of the InventoryItem for the specified Player.
	 * @param player The player who is viewing the ItemStack
	 * @return The default ItemStack if all players see the same ItemStack at all times or the output of the InventoryItem's ItemStackGetter (may be null).
	 */
	public ItemStack getItemStack(Player player){
		if(!usesGetter || player == null || getter == null){
			return itemStack;
		}else{
			ItemStack newStack = getter.getItemStack(this, player);
			if(newStack != null){
				return newStack;
			}
		}
		return itemStack;
	}
	
	/***
	 * Changes the default ItemStack representation of the InventoryItem that the player sees.
	 * @param stack The ItemStack to replace the existing one.
	 */
	public void setItemStack(ItemStack stack){
		itemStack = stack;
	}
	
	public abstract void execute(Player p, int slot);
}
