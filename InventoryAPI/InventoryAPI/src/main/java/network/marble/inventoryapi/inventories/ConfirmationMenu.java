package network.marble.inventoryapi.inventories;

import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ConfirmationMenu extends Menu{
	private ActionExecutor executor;

	public String actionDescription;
	
	/***
	 * Creates a yes/no dialogue for running the input command.
	 * @param targetPlayer Player to be confirming.
	 * @param inventoryItem Source InventoryItem.
	 * @param executor ActionExecutor task to execute.
	 * @param agreeLore Text of the yes icon description to let users know what will happen if they click it
	 * @param cancelLore Text of the no icon description to let users know what will happen if they click it
	 */
	@SuppressWarnings("serial")
	public ConfirmationMenu(Player targetPlayer, InventoryItem inventoryItem, ActionExecutor executor, String agreeLore, String cancelLore, String title, String[] args){
		super(targetPlayer, inventoryItem, 5);
		this.executor = executor;
		//this.actionDescription = actionDescription;
		
		ItemStack agree = new ItemStack(Material.EMERALD_BLOCK, 1, (short)0);
    	ItemMeta agreeMeta = agree.getItemMeta();
    	agreeMeta.setDisplayName(ChatColor.GREEN + "Yes");
    	if(agreeLore!=null) agreeMeta.setLore(Collections.singletonList(agreeLore));
    	agree.setItemMeta(agreeMeta);
    	
    	ItemStack disagree = new ItemStack(Material.REDSTONE_BLOCK, 1, (short)0);
    	ItemMeta disagreeMeta = disagree.getItemMeta();
    	disagreeMeta.setDisplayName(ChatColor.RED + "No");
    	if(cancelLore!=null) agreeMeta.setLore(Collections.singletonList(cancelLore));
    	disagree.setItemMeta(disagreeMeta);
		
    	ItemStack[] items = new ItemStack[5];
    	items[0] = agree;
    	items[4] = disagree;
		Inventory inv = InventoryAPIPlugin.getPlugin().getServer().createInventory(null, InventoryType.HOPPER, title);
		inv.setContents(items);
		targetPlayer.openInventory(inv);
	}

	public ConfirmationMenu(Player targetPlayer, InventoryItem inventoryItem, ActionExecutor executor, String agreeLore, String cancelLore, String title){
		this(targetPlayer, inventoryItem, executor, agreeLore, cancelLore, title, null);
	}
	
	/***
	 * Creates a yes/no dialogue for running the input command.
	 * @param targetPlayer Player to be confirming.
	 * @param inventoryItem Source InventoryItem.
	 * @param executor ActionExecutor task to execute.
	 * @param agreeLore Text of the yes icon description to let users know what will happen if they click it
	 * @param cancelLore Text of the no icon description to let users know what will happen if they click it
	 */
	@SuppressWarnings("serial")
	public ConfirmationMenu(Player targetPlayer, InventoryItem inventoryItem, ActionExecutor executor, String agreeLore, String cancelLore){
		this(targetPlayer, inventoryItem, executor, agreeLore, cancelLore, "Are you sure?");
	}
	
	/***
	 * Creates a yes/no dialogue for running the input command.
	 * @param targetPlayer Player to be confirming.
	 * @param inventoryItem Source InventoryItem.
	 * @param executor ActionExecutor task to execute.
	 */
	public ConfirmationMenu(Player targetPlayer, InventoryItem inventoryItem, ActionExecutor executor){
		this(targetPlayer, inventoryItem, executor, null, null);
		
	}

	@Override
	public boolean execute(int slot, int rawSlot) {
		if(rawSlot == 0){
			try{
				executor.executeAction(getTargetPlayer(), getInventoryItem(), null);
			}catch(Exception e){
				e.printStackTrace();
			}
			InventoryAPI.closePlayerCurrentMenu(getTargetPlayer());
		}else if(rawSlot == 4){
			InventoryAPI.closePlayerCurrentMenu(getTargetPlayer());
		}
		
		return false; //Clicks to the bottom of the menu are disabled in the mode
	}	
}
