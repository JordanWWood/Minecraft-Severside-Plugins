package network.marble.inventoryapi.api;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.enums.ArmorType;
import network.marble.inventoryapi.enums.InventoryVisibility;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.inventories.ArmourSet;
import network.marble.inventoryapi.inventories.Inventory;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.SubMenuInvokingItemStack;
import network.marble.inventoryapi.listeners.MenuModeListener;
import network.marble.inventoryapi.listeners.ReducedMenuModeListener;
import network.marble.inventoryapi.utils.VersionBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

public abstract class InventoryAPI {
	private static InventoryAPI versioned;

	static {
		versioned = getVersionedAPI();
    }

    private static InventoryAPI getVersionedAPI() {
		if(versioned != null) return versioned;
		try {
			return (InventoryAPI) Class.forName("network.marble.inventoryapi.impl." + VersionBootstrap.getVersion() + ".InventoryAPIImpl").newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			InventoryAPIPlugin.getPlugin().getLogger().severe("Failed to get versioned MenuModerListener: "+e.getMessage());

			for (StackTraceElement element : e.getStackTrace())
				InventoryAPIPlugin.getPlugin().getLogger().severe(element.toString());

			Bukkit.getServer().shutdown();
			return null;
		}
	}

	/***
	 * Enables server-wide menu mode, player inventories will be read only and clicking certain ItemStacks
	 * within them may trigger menus or actions depending on their implementation.
	 */
	public static void enableMenus(){
		if(!InventoryAPIPlugin.menuModeRegistered){
			HandlerList.unregisterAll(InventoryAPIPlugin.getPlugin());

			Listener versionedMenuModeListener = null;
			try {
				versionedMenuModeListener = VersionBootstrap.getClassFromType(Listener.class, "MenuModeListener");
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
				InventoryAPIPlugin.getPlugin().getLogger().severe("Failed to get versioned MenuModerListener: "+e.getMessage());

				for (StackTraceElement element : e.getStackTrace())
                    InventoryAPIPlugin.getPlugin().getLogger().severe(element.toString());

                Bukkit.getServer().shutdown();
			}

			InventoryAPIPlugin.getPlugin().getServer().getPluginManager().registerEvents(versionedMenuModeListener, InventoryAPIPlugin.getPlugin());
			InventoryAPIPlugin.getPlugin().getServer().getPluginManager().registerEvents(new MenuModeListener(), InventoryAPIPlugin.getPlugin());
			InventoryAPIPlugin.menuModeRegistered = true;
		}
    }
    
	/***
	 * Disables server-wide menu mode, players will still be given their assigned inventories but will
	 * not trigger menus or actions when clicking them, making the inventory function as it does normally.
	 */
    public static void disableMenus(){
    	HandlerList.unregisterAll(InventoryAPIPlugin.getPlugin());
		InventoryAPIPlugin.getPlugin().getServer().getPluginManager().registerEvents(new ReducedMenuModeListener(), InventoryAPIPlugin.getPlugin());
    	InventoryAPIPlugin.menuModeRegistered = false;
    }
	
	/***
	 * Creates a new empty inventory within InventoryAPI.
	 * Inventories should be created by a central plugin before being modified by additional plugins.
	 * @return Integer id of the newly created inventory.
	 */
	public static int createNewInventory(){
		int id = InventoryAPIPlugin.inventories.size();
		
		Inventory inv = new Inventory(id);
		InventoryAPIPlugin.inventories.put(id, inv);
		return id;
	}
	
	/***
	 * Copies the inventory of the specified ID into a new slot
	 * @param id The id of the Inventory to be duplicated
	 * @return The int value of the new Inventory's ID
	 */
	public static int duplicateInventory(int id){
		Inventory inv = InventoryAPIPlugin.inventories.get(id);
		
		ItemStack[] realInvItems = new ItemStack[inv.getRealInventory().length];
		
		for(int i = 0; i < inv.getRealInventory().length; ++i){
			if(inv.getRealInventory()[i] != null){
				realInvItems[i] = inv.getRealInventory()[i].clone();
			}
		}
		
		int newID = createNewInventory();
		
		for(int i = 0; i < inv.getAllInventoryItems().length; ++i){
			InventoryItem existingII = inv.getAllInventoryItems()[i];
			if(existingII instanceof ActionItemStack){
				ActionItemStack existingAIS = (ActionItemStack)existingII;
				addItemToInventory(newID, realInvItems[i], getPlayerInventorySlotXPosition(i), getPlayerInventorySlotYPosition(i), existingAIS.getExecutor(), existingAIS.isDoubleChecked());
			}else if(existingII instanceof SubMenuInvokingItemStack){
				SubMenuInvokingItemStack existingSMIIS = (SubMenuInvokingItemStack)existingII;
				addItemToInventory(newID, existingSMIIS.getItemStack(null), getPlayerInventorySlotXPosition(i), getPlayerInventorySlotYPosition(i), existingSMIIS.getInventorySize(), existingSMIIS.getContainedItems(), existingSMIIS.getInventoryName());
			}
		}
		
		return newID;
	}
	
	/***
	 * Clones all inventories being stored in InventoryAPI
	 * @return A map of the old and new IDs mapped together (key/value - old/new)
	 */
	public static Map<Integer, Integer> duplicateAllInvetories(){
		Map<Integer, Integer> inventoryIDs = new HashMap<>();
		for (Entry<Integer, Inventory> key : InventoryAPIPlugin.inventories.entrySet()) {
			int keyData = key.getKey();
			if (keyData >= 0) {
				inventoryIDs.put(key.getKey(), duplicateInventory(key.getKey()));
			}
		}
	    
	    return inventoryIDs;
	}
	
	/***
	 * Returns a primitive array of the ItemStacks contained in an inventory.
	 * @param inventoryID The ID of the inventory to get the ItemStacks from 
	 * @return ItemStack array
	 */
	public static ItemStack[] getInventoryItemStacks(int inventoryID){
		return InventoryAPIPlugin.inventories.get(inventoryID).getRealInventory();
	}
	
	public static void openMenuForPlayer(UUID uuid, Menu m){
		InventoryAPIPlugin.playerCurrentMenus.put(uuid, m);
	}
	
	public static Menu getPlayerCurrentMenu(UUID uuid){
		return InventoryAPIPlugin.playerCurrentMenus.get(uuid);
	}
	
	/***
	 * Closes the inventory being used by a player and does cleanup.
	 * @param player The player who will have their currently open (if at all) inventory screen closed.
	 */
	public static void closePlayerCurrentMenu(Player player){
		player.closeInventory();
		InventoryAPIPlugin.playerCurrentMenus.remove(player.getUniqueId());
	}
	
	/***
	 * Creates an ItemStack for use as an icon
	 * @param material The item material to show
	 * @param quantity The amount of the item
	 * @param damage The damage value of the item
	 * @param displayName The text to be displayed in place of the item name
	 * @param loreLines The lore of the item, line by line
	 * @return A new ItemStack with the given properties
	 */
	public static ItemStack createItemStack(Material material, int quantity, short damage, String displayName, ArrayList<String> loreLines, boolean hideData){
		ItemStack i = createItemStack(material, quantity, damage, displayName, loreLines);
		return mergeItemMeta(i, hideData);
	}
	
	/***
	 * Creates an ItemStack for use as an icon
	 * @param item The itemstack to display
	 * @param hideData choose to hide the default minecraft lore attributes
	 * @return A new ItemStack with the given properties
	 */
	public static ItemStack mergeItemMeta(ItemStack item, boolean hideData){
		return getVersionedAPI().mergeItemMetaImpl(item, hideData);
	}
	public abstract ItemStack mergeItemMetaImpl(ItemStack item, boolean hideData);
	
	
	/***
	 * Creates an ItemStack for use as an icon
	 * @param material The item material to show
	 * @param quantity The amount of the item
	 * @param damage The damage value of the item
	 * @param displayName The text to be displayed in place of the item name
	 * @param loreLines The lore of the item, line by line
	 * @return A new ItemStack with the given properties
	 */
	public static ItemStack createItemStack(Material material, int quantity, short damage, String displayName, ArrayList<String> loreLines){
		ItemStack item = new ItemStack(material, quantity, damage);
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(displayName);
    	if(loreLines != null){
    		meta.setLore(loreLines);
    	}
    	item.setItemMeta(meta);
		return item;
	}
	
	/***
	 * Sets the input player's InventoryAPI inventory id to the given integer id and then updates their inventory to display this inventory's items.
	 * @param player The player to change the inventory of.
	 * @param inventoryID The ID of the InventoryAPI inventory.
	 * @return
	 */
	public static boolean setPlayerInventory(Player player, int inventoryID){
		if(InventoryAPIPlugin.inventories.get(inventoryID) != null){
			if(player != null){
				Inventory inv = InventoryAPIPlugin.inventories.get(inventoryID);
				ItemStack[] realItemStacks = inv.getRealInventory();
				
				for(Entry<Integer, InventoryItem> ii:InventoryAPIPlugin.globalItems.entrySet()){
					int slot = ii.getKey();
					if(realItemStacks[slot] == null){
						realItemStacks[slot] = ii.getValue().getItemStack(player);
					}
				}
				
				if(InventoryAPIPlugin.playerInventories.get(player.getUniqueId()) != null){
					InventoryAPIPlugin.playerInventories.replace(player.getUniqueId(), inventoryID);
				}else{
					InventoryAPIPlugin.playerInventories.put(player.getUniqueId(), inventoryID);
				}
				
				refreshPlayerView(player);
				player.closeInventory();
				return true;
			}else{
				InventoryAPIPlugin.getPlugin().getLogger().severe("Player object is null in setPlayerInventoryType.");
			}
		}
		
		return false;
	}

	/***
	 * Rebuilds the players inventory with priority of InventoryItems from the players assigned inventory being shown over global items 
	 * @param player The player who needs their inventory refreshed
	 * @return
	 */
	public static boolean refreshPlayerView(Player player){
		if(player!=null){
			Bukkit.getScheduler().runTaskAsynchronously(InventoryAPIPlugin.getPlugin(), () -> {
				Inventory inv = InventoryAPIPlugin.inventories.get(InventoryAPIPlugin.playerInventories.get(player.getUniqueId()));
				ItemStack[] realItemStacks = new ItemStack[36];
				InventoryItem[] inventoryItems = inv.getAllInventoryItems();

				InventoryAPIPlugin.globalItems.forEach((key, value) -> realItemStacks[key] = value.getItemStack(player));

				for (int i = 0; i < inventoryItems.length; i++) {
					if (inventoryItems[i] != null) {
						realItemStacks[i] = inventoryItems[i].getItemStack(player);
					}
				}

				player.getInventory().setContents(realItemStacks);
				refreshPlayerArmour(player);
				refreshPlayerOffHandItem(player);
				player.updateInventory();
			});

			return true;
		}else{
			return false;
		}
	}
	
	/***
	 * Rebuilds the players inventory armour slots with priority of InventoryItems from the players assigned inventory being shown over global items 
	 * @param player The player who needs their inventory refreshed
	 * @return
	 */
	public static boolean refreshPlayerArmour(Player player){
		if(player!=null){
			//Generate armour
			ItemStack[] armourItems = InventoryAPIPlugin.globalArmour.getItemStacks(player);
			if(InventoryAPIPlugin.playerArmour.containsKey(player.getUniqueId())){
				ItemStack[] playerArmour = InventoryAPIPlugin.playerArmour.get(player.getUniqueId()).getItemStacks(player);
				for(int i = 0; i < playerArmour.length; i++){
					if(playerArmour[i]!=null && playerArmour[i].getType() != Material.AIR){
						armourItems[i] = playerArmour[i];
					}
				}
			}
			player.getInventory().setArmorContents(armourItems);
			return true;
		}else{
			return false;
		}
	}
	
	/***
	 * Rebuilds a players inventory armour slot with priority of an InventoryItem from the players assigned inventory being shown over a global item
	 * @param player The player who needs their inventory refreshed
	 * @return
	 */
	public static boolean refreshPlayerArmour(Player player, ArmorType type){
		if(player!=null){
			//Generate armour
			ItemStack armourItem = null;
			if(InventoryAPIPlugin.globalArmour.getArmorInventoryItem(type) != null){
				armourItem = InventoryAPIPlugin.globalArmour.getArmorInventoryItem(type).getItemStack(player);
			}
			if(InventoryAPIPlugin.playerArmour.containsKey(player.getUniqueId())){
				InventoryItem playerArmour = InventoryAPIPlugin.playerArmour.get(player.getUniqueId()).getArmorInventoryItem(type);
				
				if(playerArmour != null && armourItem==null){
					armourItem = playerArmour.getItemStack(player);
				}
			}
			switch(type){
				case BOOTS:
					player.getInventory().setBoots(armourItem);
					break;
				case CHEST:
					player.getInventory().setChestplate(armourItem);
					break;
				case HELMET:
					player.getInventory().setHelmet(armourItem);
					break;
				case LEGS:
					player.getInventory().setLeggings(armourItem);
					break;
				default:
					return false;
			}
			return true;
		}else{
			return false;
		}
	}
	
	/***
	 * Updates the InventoryItem of a specific player inventory slot.
	 * @param player The player who needs their inventory refreshed
	 * @param x Coordinate marking the x (left to right) position of the item to update. x{1 <= x <= 9}
	 * @param y Coordinate marking the y (top to bottom) position of the item to update. y{1 <= y <= 4}
	 * @throws InvalidSlotCoordinateException Must follow the 1 <= x <= 9 and 1 <= y <= 4 rule.
	 */
	public static void refreshPlayerItem(Player player, int x, int y) throws InvalidSlotCoordinateException{
		if(x + y < 2 || x > 9 || y > 4) throw new InvalidSlotCoordinateException(x,y);
		if(player != null){
			int realSlot = calculatePlayerInventorySlot(x, y);
			refreshPlayerItem(player, realSlot);
		}
	}
	
	/***
	 * Updates the InventoryItem of a specific player inventory slot.
	 * @param player The player who needs their inventory refreshed
	 * @param slot The slot of the players inventory to update
	 */
	public static void refreshPlayerItem(Player player, int slot){
		if(player != null){
			Inventory inv = InventoryAPIPlugin.inventories.get(InventoryAPIPlugin.playerInventories.get(player.getUniqueId()));
			if(inv==null) return;
			InventoryItem groupItem = inv.getInventoryItem(slot);
			
			InventoryItem item;
			item = InventoryAPIPlugin.globalItems.get(slot);
			if(groupItem != null) item = groupItem;//TODO player level visibility items
			player.getInventory().setItem(slot, item.getItemStack(player));
			player.updateInventory();
		}
	}
	
	/***
	 * Returns the InventoryItem visible to the specified player in their off-hand
	 * @param player The player the off-hand item is visible to
	 * @return The InventoryItem being displayed to the player in their off-hand
	 */
	public static InventoryItem getOffHandItem(Player player){
		InventoryItem groupItem = InventoryAPIPlugin.inventoryOffHandItems.get(getPlayerInventoryID(player.getUniqueId()));
		if(groupItem != null) return groupItem;
		return InventoryAPIPlugin.globalOffHandItem;
	}
	
	/***
	 * Returns the InventoryItem visible to the specified player in the specified slot
	 * @param player The player the item is visible to
	 * @param x The x position of the slot of the players inventory to get from
	 * @param y The y position of the slot of the players inventory to get from
	 * @return The InventoryItem being displayed to the player
	 */
	public static InventoryItem getItemAtSlot(Player player, int x, int y){
		return getItemAtSlot(player, calculatePlayerInventorySlot(x, y));
	}
	
	/***
	 * Returns the InventoryItem visible to the specified player in the specified slot
	 * @param player The player the item is visible to
	 * @param slot The slot of the players inventory to get from
	 * @return The InventoryItem being displayed to the player
	 */
	public static InventoryItem getItemAtSlot(Player player, int slot){
		InventoryItem item = null;
		if(player != null){
			Inventory inv = InventoryAPIPlugin.inventories.get(InventoryAPIPlugin.playerInventories.get(player.getUniqueId()));
			InventoryItem groupItem = inv.getInventoryItem(slot);
			
			item = InventoryAPIPlugin.globalItems.get(slot);
			if(groupItem != null) item = groupItem; //TODO player level visibility items
		}
		
		return item;
	}
	
	/***
	 * Updates the InventoryItem in a players off-hand item slot.
	 * @param player The player who needs their inventory refreshed
	 */
	public static void refreshPlayerOffHandItem(Player player){
		getVersionedAPI().refreshPlayerOffHandItemImpl(player);
	}
	public abstract void refreshPlayerOffHandItemImpl(Player player);

	public static void setGlobalArmorItem(ArmorType type, InventoryItem inventoryItem){
		InventoryAPIPlugin.globalArmour.setInventoryItem(type, inventoryItem);
	}
	
	public static boolean setPlayerArmorItem(Player player, ArmorType type, InventoryItem inventoryItem){
		if(player != null){
			ArmourSet armor = InventoryAPIPlugin.playerArmour.get(player.getUniqueId());
			if(armor != null){
				armor.setInventoryItem(type, inventoryItem);
				
				player.getInventory().setArmorContents(armor.getItemStacks());
				player.updateInventory();
				
				return true;
			}
		}else{
			InventoryAPIPlugin.getPlugin().getLogger().severe("Player object is null in setPlayerArmorItem.");
		}
		return false;
	}
	
	/***
	 * 
	 * @param secondaryViewLength Amount of inventory slots in secondary view (e.g. 54 for a double chest).
	 * @param rawSlot The raw slot value
	 * @return
	 */
	public static boolean isInPlayerInventorySection(int secondaryViewLength, int rawSlot){
		return rawSlot >= secondaryViewLength;
	}
	
	/***
	 * Returns the true slot value of a player inventory.
	 * @param xPos Slot position on x axis of main inventory section. This should be 1-9.
	 * @param yPos Slot position on y axis of main inventory section. This should be 1-4.
	 * @return The slot value.
	 * //@exception Returns -1.
	 */
	public static int calculatePlayerInventorySlot(int xPos, int yPos){
		int slot = -1;
		
		if(yPos >= 1 && yPos <= 4 && xPos >= 1 && xPos <= 9){
			if (yPos == 4){
				slot = xPos - 1;
			}else{
				slot = 9 + (yPos * 9 - 9) + (xPos - 1); //9 is first position in top row
			}
		}
		
		return slot;
	}
	
	/***
	 * Returns the x axis position from a slot value of a player inventory.
	 * @param slot The slot of the item.
	 * @return The x axis slot value.
	 * //@exception Returns -1.
	 */
	public static int getPlayerInventorySlotXPosition(int slot){
		int xslot = -1;
		
		if(slot >= 0 && slot < 36){
			xslot = (slot % 9) + 1;
		}
		
		return xslot;
	}
	
	/***
	 * Returns the y axis position from a slot value of a player inventory.
	 * @param slot The slot of the item.
	 * @return The y axis slot value.
	 * //@exception Returns -1.
	 */
	public static int getPlayerInventorySlotYPosition(int slot){
		int ySlot = -1;
		
		if(slot<9){
			ySlot = 4;
		}else if(slot < 36){
			ySlot = (slot - (slot % 9)) / 9;
		}
		
		return ySlot;
	}
	
	/***
	 * Returns the true slot value of a chest inventory.
	 * @param xPos Slot position on x axis of main inventory section. This should be 1-9.
	 * @param yPos Slot position on y axis of main inventory section. This could be 1-3 or 1-6 depending on chest size.
	 * @return The slot value.
	 * //@exception Returns -1.
	 */
	public static int calculateChestInventorySlot(int xPos, int yPos){
		int slot = -1;
		
		if(yPos >= 1 && yPos <= 6 && xPos >= 1 && xPos <= 9){
			slot = (yPos * 9 - 9) + (xPos - 1); //9 is first position in top row
		}
		
		return slot;
	}
	
	/***
	 * Gets the id of the inventory assigned to the input player's UUID.
	 * @param playerUUID The player in question's UUID
	 * @return Integer form of inventoryID. By default, if a player has not been assigned an inventory this will assign them default inventory 0 and return as such.
	 */
	public static Integer getPlayerInventoryID(UUID playerUUID){
		Integer inventoryID = InventoryAPIPlugin.playerInventories.get(playerUUID);
		
		if(inventoryID == null){
			InventoryAPIPlugin.playerInventories.put(playerUUID, 0);
			inventoryID = 0;
		}
		
		return inventoryID;
	}
	
	/***
	 * Adds a given InventoryItem to a specific inventory group
	 * @param inventoryID The ID of the inventory type that this itemstack resides in.
	 * @param item InventoryItem to add
	 * @param xPos X-axis position of itemstack in player inventory. This must be in the range 1-9. This range is from left to right of the inventory.
	 * @param yPos Y-axis position of itemstack in player inventory. This must be in the range 1-4. This range is from top to bottom of the inventory.
	 */
	public static void addItemToInventory(int inventoryID, InventoryItem item, int xPos, int yPos) throws SlotCollisionException{
		InventoryAPIPlugin.inventories.get(inventoryID).addToInventory(item, xPos, yPos);
	}
	
	/***
	 * Adds a given InventoryItem to a specific inventory group's off hand
	 * @param inventoryID The ID of the inventory type that this itemstack will reside in the off-hand of.
	 * @param item InventoryItem to add
	 */
	public static void addItemToInventoryOffHand(int inventoryID, InventoryItem item){
		InventoryAPIPlugin.inventoryOffHandItems.put(inventoryID, item);
	}
	
	/***
	 * Generates a simple command executor itemstack (ActionItemStack)
	 * @param inventoryID The ID of the inventory type that this itemstack resides in.
	 * @param itemStack Vanilla ItemStack to display in specified slot of player inventory.
	 * @param xPos X-axis position of itemstack in player inventory. This must be in the range 1-9. This range is from left to right of the inventory.
	 * @param yPos Y-axis position of itemstack in player inventory. This must be in the range 1-4. This range is from top to bottom of the inventory.
	 * @param executor The ActionExecutor to call when this item is triggered (usually clicked on).
	 */
	public static void addItemToInventory(int inventoryID, ItemStack itemStack, int xPos, int yPos, ActionExecutor executor, boolean requiresConfirmation){
		ActionItemStack apiItemStack = new ActionItemStack(itemStack, executor, requiresConfirmation);
		InventoryAPIPlugin.inventories.get(inventoryID).addToInventory(apiItemStack, xPos, yPos);
	}
	
	/***
	 * Generates a command executor itemstack (ActionItemStack) with an ItemStackGetter interface
	 * @param inventoryID The ID of the inventory type that this itemstack resides in.
	 * @param itemStack Vanilla ItemStack to display in specified slot of player inventory.
	 * @param xPos X-axis position of itemstack in player inventory. This must be in the range 1-9. This range is from left to right of the inventory.
	 * @param yPos Y-axis position of itemstack in player inventory. This must be in the range 1-4. This range is from top to bottom of the inventory.
	 * @param executor The ActionExecutor to call when this item is triggered (usually clicked on).
	 */
	public static void addItemToInventory(int inventoryID, ItemStack itemStack, int xPos, int yPos, ActionExecutor executor, boolean requiresConfirmation, ItemStackGetter getter){
		ActionItemStack apiItemStack = new ActionItemStack(itemStack, executor, requiresConfirmation, getter);
		InventoryAPIPlugin.inventories.get(inventoryID).addToInventory(apiItemStack, xPos, yPos);
	}
	
	/***
	 * Generates a SubMenuInvokingItemStack
	 * @param inventoryID The ID of the inventory type that this itemstack resides in.
	 * @param itemStack Vanilla ItemStack to display in specified slot of player inventory.
	 * @param xPos X-axis position of itemstack in player inventory. This must be in the range 1-9. This range is from left to right of the inventory.
	 * @param yPos Y-axis position of itemstack in player inventory. This must be in the range 1-4. This range is from top to bottom of the inventory.
	 * //@param inventoryType The type of inventory to be displayed on item use.
	 * @param containedItems The InventoryItems contained within
	 * @param inventoryName Title to display at top of inventory
	 */
	public static SubMenuInvokingItemStack addItemToInventory(int inventoryID, ItemStack itemStack, int xPos, int yPos, int inventorySize, InventoryItem[] containedItems, String inventoryName){
		SubMenuInvokingItemStack apiItemStack = new SubMenuInvokingItemStack(itemStack, inventorySize, containedItems, inventoryName);
		InventoryAPIPlugin.inventories.get(inventoryID).addToInventory(apiItemStack, xPos, yPos);
		return apiItemStack;
	}
	
	/***
	 * Generates a simple command executor itemstack (ActionItemStack)
	 * @param itemStack Vanilla ItemStack to display in specified slot of player inventory.
	 * @param xPos X-axis position of itemstack in player inventory. This must be in the range 1-9. This range is from left to right of the inventory.
	 * @param yPos Y-axis position of itemstack in player inventory. This must be in the range 1-4. This range is from top to bottom of the inventory.
	 * @param executor The ActionExecutor to call when this item is triggered (usually clicked on).
	 * @param inventoryIDs The IDs of the inventories that this itemstack resides in.
	 */
	public static void addItemToInventories(ItemStack itemStack, int xPos, int yPos, ActionExecutor executor, boolean requiresConfirmation, int... inventoryIDs){
		ActionItemStack apiItemStack = new ActionItemStack(itemStack, executor, requiresConfirmation);
		for (int i = 0; i < inventoryIDs.length; ++i){
			InventoryAPIPlugin.inventories.get(inventoryIDs[i]).addToInventory(apiItemStack, xPos, yPos);
		}
	}
	
	/***
	 * Generates a SubMenuInvokingItemStack
	 * //@param inventoryID The ID of the inventory type that this itemstack resides in.
	 * @param itemStack Vanilla ItemStack to display in specified slot of player inventory.
	 * @param xPos X-axis position of itemstack in player inventory. This must be in the range 1-9. This range is from left to right of the inventory.
	 * @param yPos Y-axis position of itemstack in player inventory. This must be in the range 1-4. This range is from top to bottom of the inventory.
	 * //@param inventoryType The type of inventory to be displayed on item use.
	 * @param containedItems The InventoryItems contained within
	 * @param inventoryName
	 */
	public static SubMenuInvokingItemStack addItemToInventories(ItemStack itemStack, int xPos, int yPos, int inventorySize, InventoryItem[] containedItems, String inventoryName, int... inventoryIDs){
		SubMenuInvokingItemStack apiItemStack = new SubMenuInvokingItemStack(itemStack, inventorySize, containedItems, inventoryName);
		for (int inventoryID : inventoryIDs) {
			InventoryAPIPlugin.inventories.get(inventoryID).addToInventory(apiItemStack, xPos, yPos);
		}
		return apiItemStack;
	}
	
	/***
	 * Makes an InventoryItem visible in all players inventories
	 * @param inventoryItem The InventoryItem to make global
	 * @param xPos The inventory slot's x (column/horizontal) position in which the item will be placed.
	 * @param yPos The inventory slot's y (row/vertical) position in which the item will be placed.
	 * @throws SlotCollisionException thrown if the specified slot has been filled by another plugin
	 */
	public static void addGlobalInventoryItem(InventoryItem inventoryItem, int xPos, int yPos) throws SlotCollisionException{
		int slot = calculatePlayerInventorySlot(xPos, yPos);
		if(!InventoryAPIPlugin.globalItems.containsKey(slot) && slot >= 0 && slot < 36){
			InventoryAPIPlugin.globalItems.put(calculatePlayerInventorySlot(xPos, yPos), inventoryItem);
		}else{
			throw new SlotCollisionException(slot, InventoryVisibility.GLOBAL);
		}
	}
	
	/***
	 * Sets the InventoryItem to render in all player's off hands.
	 * @param inventoryItem The InventoryItem to render
	 */
	public static void setGlobalOffHandInventoryItem(InventoryItem inventoryItem){
		InventoryAPIPlugin.globalOffHandItem = inventoryItem;
	}
	
	/**
	 * Renames a clone of the ItemStack with the new display name
	 */
	public static ItemStack renameItemstack(ItemStack original, String newName) {
		ItemStack is = original.clone();
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(newName);
		is.setItemMeta(im);
		return is;
	}

    public static ItemStack appendLore(ItemStack original, String[] lore){
	    if(lore==null) return original;
	    
	    return appendLore(original, Arrays.asList(lore));
    }

	public static ItemStack appendLore(ItemStack original, List<String> lore){
        if(lore==null) return original;

		ItemStack is = original.clone();
		ItemMeta im = is.getItemMeta();
		List<String> finalLore = im.hasLore() ? im.getLore() : new ArrayList<>();
		finalLore.addAll(lore);
		im.setLore(finalLore);
		is.setItemMeta(im);

		return is;
	}

	/***
	 * Assigns an NBT texture to a skull ItemStack
	 *
	 * @param url The uuid of the owning player
	 * @return A textured skull
	 */
	public static ItemStack setSkullTexture(String url) {
		return getVersionedAPI().setSkullTextureImpl(url);
	}
	public abstract ItemStack setSkullTextureImpl(String url);

	/**
	 * Deletes the given players player.dat in all worlds loaded
	 * @param playerId The UUID of the player to erase
	 */
	public static void deletePlayerInventoryData(UUID playerId){
		if(InventoryAPIPlugin.getConfigModel().deleteDat){
			for(World w : Bukkit.getWorlds()){
				File f = new File(InventoryAPIPlugin.worldPlayersFolders.get(w.getName()), playerId.toString());
				if(f.exists()){
					f.delete();
				}
			}
		}
	}

	/**
	 * Executes the processor of a given players open menu slot
	 * @param p The player whos menu is being triggered
	 * @param slot The slot in the menu
	 */
	public static void clickProcess(Player p, int slot){
		if(InventoryAPIPlugin.playerInventories.containsKey(p.getUniqueId())){
			Integer invID = InventoryAPIPlugin.playerInventories.get(p.getUniqueId());
			if(InventoryAPIPlugin.inventories.containsKey(invID)){
				InventoryItem item = (slot == 40 ? InventoryAPI.getOffHandItem(p) : InventoryAPIPlugin.inventories.get(invID).getInventoryItem(slot));
				if(item != null){//Check group level inventory first
					try{
						item.execute(p, slot);
					}catch(Exception e){
						e.printStackTrace();
					}
				}else if(InventoryAPIPlugin.globalItems.containsKey(slot)){
					try{
						InventoryAPIPlugin.globalItems.get(slot).execute(p, slot);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}
