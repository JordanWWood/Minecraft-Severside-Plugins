package network.marble.vanity.managers;

import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.vanity.api.Slot;
import network.marble.vanity.menus.MenuItems;
import network.marble.vanity.menus.getters.WornItemGetter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import net.md_5.bungee.api.ChatColor;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.api.SlotCollisionException;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.SubMenuInvokingItemStack;

public class MenuManager {
    private InventoryItem[] mainMenu;
    private InventoryItem[] armourMenu;

    // Items on amour
    private InventoryItem[] helmetItems;
    private InventoryItem[] shirtItems;
    private InventoryItem[] legsItems;
    private InventoryItem[] shoesItems;

    public MenuManager() {
        mainMenu = new InventoryItem[InventoryType.CHEST.getDefaultSize()];
        armourMenu = new InventoryItem[InventoryType.HOPPER.getDefaultSize()];
    }

    public void buildInventoryMenus() {
        ItemStack mainMenuIS = InventoryAPI.createItemStack(Material.INK_SACK, 1, (short) 4, ChatColor.GOLD + "" + ChatColor.BOLD + "Vanity", null, true);
        ItemStack armourMenuIS = InventoryAPI.createItemStack(Material.CHAINMAIL_CHESTPLATE, 1, (short) 1, ChatColor.GOLD + "Set Armour", null, true);

        mainMenu[0] = MenuItems.HAT.getItem();
        mainMenu[1] = MenuItems.EFFECT.getItem();
        mainMenu[2] = MenuItems.TRAIL.getItem();

        armourMenu[0] = MenuItems.HELMET.getItem();
        armourMenu[1] = MenuItems.CHEST.getItem();
        armourMenu[2] = MenuItems.LEGS.getItem();
        armourMenu[3] = MenuItems.BOOTS.getItem();

        mainMenu[3] = new SubMenuInvokingItemStack(armourMenuIS, InventoryType.HOPPER, armourMenu, "Select a category:");

        SubMenuInvokingItemStack apiItemStack = new SubMenuInvokingItemStack(mainMenuIS, InventoryType.CHEST, mainMenu, "Select a category:");

        try {
            InventoryAPI.addGlobalInventoryItem(apiItemStack, 1, 4);
        } catch (SlotCollisionException e) {
            e.printStackTrace();
        }

        ActionItemStack head = new ActionItemStack(new ItemStack(Material.AIR), null, false, new WornItemGetter(Slot.HEAD));
        ActionItemStack chest = new ActionItemStack(new ItemStack(Material.AIR), null, false, new WornItemGetter(Slot.CHEST));
        ActionItemStack legs = new ActionItemStack(new ItemStack(Material.AIR), null, false, new WornItemGetter(Slot.LEGS));
        ActionItemStack boots = new ActionItemStack(new ItemStack(Material.AIR), null, false, new WornItemGetter(Slot.BOOTS));
    }
}