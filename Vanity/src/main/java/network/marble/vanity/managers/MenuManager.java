package network.marble.vanity.managers;

import network.marble.inventoryapi.enums.ArmorType;
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

    public MenuManager() {
        mainMenu = new InventoryItem[InventoryType.HOPPER.getDefaultSize()];
        armourMenu = new InventoryItem[InventoryType.HOPPER.getDefaultSize()];
    }

    public void buildInventoryMenus() {
        ItemStack mainMenuIS = InventoryAPI.createItemStack(Material.INK_SACK, 1, (short) 4, ChatColor.GOLD + "" + ChatColor.BOLD + "Vanity", null, true);
        ItemStack armourMenuIS = InventoryAPI.createItemStack(Material.ELYTRA, 1, (short) 1, ChatColor.GOLD + "Set Clothing", null, true);

        mainMenu[0] = MenuItems.HAT.getItem();
        mainMenu[1] = MenuItems.EFFECT.getItem();
        mainMenu[2] = MenuItems.TRAIL.getItem();

        armourMenu[0] = MenuItems.HEAD.getItem();
        armourMenu[1] = MenuItems.CHEST.getItem();
        armourMenu[2] = MenuItems.LEGS.getItem();
        armourMenu[3] = MenuItems.BOOTS.getItem();

        mainMenu[3] = new SubMenuInvokingItemStack(armourMenuIS, InventoryType.HOPPER, armourMenu, "Select a category:");

        SubMenuInvokingItemStack apiItemStack = new SubMenuInvokingItemStack(mainMenuIS, InventoryType.HOPPER, mainMenu, "Select a category:");

        try {
            InventoryAPI.addGlobalInventoryItem(apiItemStack, 1, 4);
        } catch (SlotCollisionException e) {
            e.printStackTrace();
        }

        ActionItemStack head = new ActionItemStack(new ItemStack(Material.STONE), null, false, new WornItemGetter(Slot.HEAD));
        ActionItemStack chest = new ActionItemStack(new ItemStack(Material.STONE), null, false, new WornItemGetter(Slot.CHEST));
        ActionItemStack legs = new ActionItemStack(new ItemStack(Material.STONE), null, false, new WornItemGetter(Slot.LEGS));
        ActionItemStack boots = new ActionItemStack(new ItemStack(Material.STONE), null, false, new WornItemGetter(Slot.BOOTS));

        InventoryAPI.setGlobalArmorItem(ArmorType.HELMET, head);
        InventoryAPI.setGlobalArmorItem(ArmorType.CHEST, chest);
        InventoryAPI.setGlobalArmorItem(ArmorType.LEGS, legs);
        InventoryAPI.setGlobalArmorItem(ArmorType.BOOTS, boots);
    }
}