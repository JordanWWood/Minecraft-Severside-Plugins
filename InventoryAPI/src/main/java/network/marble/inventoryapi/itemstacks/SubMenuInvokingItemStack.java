package network.marble.inventoryapi.itemstacks;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.inventories.SubMenu;

public class SubMenuInvokingItemStack extends InventoryItem{
	private InventoryItem[] containedItems;
	private ItemStack itemStack;
	private String inventoryName;
	private ItemStack[] realInventory;
	int inventorySize;
	InventoryType inventoryType;
	
	public SubMenuInvokingItemStack(ItemStack itemStack, InventoryType inventoryType, InventoryItem[] containedItems, String inventoryName) {
		this(itemStack, inventoryType.getDefaultSize(), containedItems, inventoryName);
		this.inventoryType = inventoryType;
	}
	
	public SubMenuInvokingItemStack(ItemStack itemStack, InventoryType inventoryType, InventoryItem[] containedItems, String inventoryName, ItemStackGetter getter) {
		this(itemStack, inventoryType.getDefaultSize(), containedItems, inventoryName, getter);
		this.inventoryType = inventoryType;
	}
	
	public SubMenuInvokingItemStack(ItemStack itemStack, int inventorySize, InventoryItem[] containedItems, String inventoryName) {
		super(itemStack);
		construct(itemStack, inventorySize, containedItems, inventoryName);
	}
	
	public SubMenuInvokingItemStack(ItemStack itemStack, int inventorySize, InventoryItem[] containedItems, String inventoryName, ItemStackGetter getter) {
		super(itemStack, getter);
		construct(itemStack, inventorySize, containedItems, inventoryName);
	}
	
	private void construct(ItemStack itemStack, int inventorySize, InventoryItem[] containedItems, String inventoryName){
		this.itemStack = itemStack;
		this.inventorySize = inventorySize;
		this.containedItems = containedItems;
		this.inventoryName = inventoryName;
		
		realInventory = new ItemStack[containedItems.length];
		for(int i = 0; i < containedItems.length; ++i){
			if(containedItems[i] != null){
				realInventory[i] = containedItems[i].getItemStack(null);
			}
		}
	}
	
	public boolean insertInventoryItem(InventoryItem inventoryItem, int slot){
		if(inventoryItem != null && slot >=0 && slot < inventorySize){
			if(containedItems[slot] == null){
				if(realInventory[slot] == null){
					containedItems[slot] = inventoryItem;
					realInventory[slot] = inventoryItem.getItemStack(null);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public InventoryItem getContainedItem(int slot){
		return containedItems[slot];
	}
	
	public InventoryItem[] getContainedItems(){
		return containedItems;
	}
	
	public InventoryType getInventoryType(){
		return inventoryType;
	}
	
	public ItemStack getOriginItemStack(){
		return itemStack;
	}
	
	public String getInventoryName(){
		return inventoryName;
	}

	public ItemStack[] getRealInventory(Player player) {
		if(player == null){
			return realInventory;
		}else{
			ItemStack[] playerItems = new ItemStack[realInventory.length];
			for(int i = 0; i < containedItems.length; ++i){
				if(containedItems[i] != null){
					playerItems[i] = containedItems[i].getItemStack(player);
				}
			}
			
			return playerItems;
		}
	}
	
	public int getInventorySize() {
		return inventorySize;
	}

	@Override
	public void execute(Player p, int slot) {
		InventoryAPIPlugin.playerCurrentMenus.put(p.getUniqueId(), new SubMenu(p, this));
	}
}
