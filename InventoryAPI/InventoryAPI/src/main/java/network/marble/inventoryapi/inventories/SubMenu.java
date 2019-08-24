package network.marble.inventoryapi.inventories;

import org.bukkit.entity.Player;
import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.SubMenuInvokingItemStack;

public class SubMenu extends Menu{
	private InventoryItem[] items;
	private Player p;

	public SubMenu(Player targetPlayer, SubMenuInvokingItemStack sourceItem, String renderedTitle){
		super(targetPlayer, sourceItem, sourceItem.getInventorySize());
		p = targetPlayer;
		items = sourceItem.getContainedItems();
		org.bukkit.inventory.Inventory inv;
		if(sourceItem.getInventoryType() == null) inv = InventoryAPIPlugin.getPlugin().getServer().createInventory(p, sourceItem.getInventorySize(), renderedTitle);
		else inv = InventoryAPIPlugin.getPlugin().getServer().createInventory(p, sourceItem.getInventoryType(), renderedTitle);
		inv.setContents(sourceItem.getRealInventory(targetPlayer));
		targetPlayer.openInventory(inv);
	}

	public SubMenu(Player targetPlayer, SubMenuInvokingItemStack sourceItem) {
		this(targetPlayer, sourceItem, sourceItem.getInventoryName());
	}

	@Override
	public boolean execute(int slot, int rawSlot) {
		if(slot == rawSlot){//Validate the click is in the top of the inventory
			if(items.length > slot && items[slot] != null) items[slot].execute(p, slot);
			return false;
		}else{
			return true;
		}
	}
}
