package network.marble.inventoryapi.impl.v1_12_R1;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.SearchingMenu;
import network.marble.inventoryapi.inventories.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@Deprecated
public class SearchMenuImpl implements SearchingMenu {
    public void build() {
        Inventory inv = new Inventory(-1);
		InventoryAPIPlugin.inventories.put(-1, inv);

		//alphabetical head generator
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String[] skullUsers = new String[]{"white"};//TODO get real list of letter headed users
		for(int i = 0; i < 26; ++i){
			String letter = alphabet.substring(i, i+1);

			//Create meta and set skull to look like player
			SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
			meta.setOwner(skullUsers[0]);
			//Display username as item name
			meta.setDisplayName(ChatColor.GOLD + "Players Starting With " + letter);
	        //Apply meta
			ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			stack.setItemMeta(meta);

			//InventoryAPI.addItemToInventory(-1, stack, InventoryAPI.getPlayerInventorySlotXPosition(i+9), InventoryAPI.getPlayerInventorySlotYPosition(i+9), "", false);
		}

		SkullMeta meta = (SkullMeta)Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		meta.setOwner(skullUsers[0]);
		meta.setDisplayName(ChatColor.GOLD + "Players Starting With Symbols");
		ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
		stack.setItemMeta(meta);

		//InventoryAPI.addItemToInventory(-1, stack, 9, 3, "", false);

		//Page Controls
		ItemStack firstPageIS = new ItemStack(Material.SEEDS, 1, (short)0);
    	ItemMeta firstPageMeta = firstPageIS.getItemMeta();
    	firstPageMeta.setDisplayName(ChatColor.GOLD + "First Page");
    	firstPageIS.setItemMeta(firstPageMeta);

    	ItemStack backHundredIS = new ItemStack(Material.GOLD_BLOCK, 1, (short)0);
    	ItemMeta backHundredMeta = backHundredIS.getItemMeta();
    	backHundredMeta.setDisplayName(ChatColor.GOLD + "Back 100 Pages");
    	backHundredIS.setItemMeta(backHundredMeta);

    	ItemStack backTenIS = new ItemStack(Material.REDSTONE_BLOCK, 1, (short)0);
    	ItemMeta backTenMeta = backTenIS.getItemMeta();
    	backTenMeta.setDisplayName(ChatColor.GOLD + "Back 10 Pages");
    	backTenIS.setItemMeta(backTenMeta);

    	ItemStack previousPageIS = new ItemStack(Material.POTATO_ITEM, 1, (short)0);
    	ItemMeta previousPageMeta = previousPageIS.getItemMeta();
    	previousPageMeta.setDisplayName(ChatColor.GOLD + "Previous Page");
    	previousPageIS.setItemMeta(previousPageMeta);

    	ItemStack refreshIS = new ItemStack(Material.NETHER_STAR, 1, (short)0);
    	ItemMeta refreshMeta = refreshIS.getItemMeta();
    	refreshMeta.setDisplayName(ChatColor.GOLD + "Refresh");
    	refreshIS.setItemMeta(refreshMeta);

		ItemStack nextPageIS = new ItemStack(Material.BAKED_POTATO, 1, (short)0);
    	ItemMeta nextPageMeta = nextPageIS.getItemMeta();
    	nextPageMeta.setDisplayName(ChatColor.GOLD + "Next Page");
    	nextPageIS.setItemMeta(nextPageMeta);

    	ItemStack forwardTenIS = new ItemStack(Material.DIAMOND_BLOCK, 1, (short)0);
    	ItemMeta forwardTenMeta = forwardTenIS.getItemMeta();
    	forwardTenMeta.setDisplayName(ChatColor.GOLD + "Forward 10 Pages");
    	forwardTenIS.setItemMeta(forwardTenMeta);

    	ItemStack forwardHundredIS = new ItemStack(Material.EMERALD_BLOCK, 1, (short)0);
    	ItemMeta forwardHundredMeta = forwardHundredIS.getItemMeta();
    	forwardHundredMeta.setDisplayName(ChatColor.GOLD + "Forward 100 Pages");
    	forwardHundredIS.setItemMeta(forwardHundredMeta);

    	ItemStack lastPageIS = new ItemStack(Material.EYE_OF_ENDER, 1, (short)0);
    	ItemMeta lastPageMeta = lastPageIS.getItemMeta();
    	lastPageMeta.setDisplayName(ChatColor.GOLD + "Last Page");
    	lastPageIS.setItemMeta(lastPageMeta);

		/*InventoryAPI.addItemToInventory(-1, firstPageIS, 1, 4, "", false);
		InventoryAPI.addItemToInventory(-1, backHundredIS, 2, 4, "", false);
		InventoryAPI.addItemToInventory(-1, backTenIS, 3, 4, "", false);
		InventoryAPI.addItemToInventory(-1, previousPageIS, 4, 4, "", false);
		InventoryAPI.addItemToInventory(-1, refreshIS, 5, 4, "", false);
		InventoryAPI.addItemToInventory(-1, nextPageIS, 6, 4, "", false);
		InventoryAPI.addItemToInventory(-1, forwardTenIS, 7, 4, "", false);
		InventoryAPI.addItemToInventory(-1, forwardHundredIS, 8, 4, "", false);
		InventoryAPI.addItemToInventory(-1, lastPageIS, 9, 4, "", false);*/
    }
}
