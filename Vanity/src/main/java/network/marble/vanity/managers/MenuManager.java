package network.marble.vanity.managers;

import network.marble.inventoryapi.enums.ArmorType;
import network.marble.inventoryapi.inventories.Inventory;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.StandardItemStack;
import network.marble.messageapi.api.FontFormat;
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
import org.bukkit.inventory.meta.ItemMeta;

public class MenuManager {
    private InventoryItem[] mainMenu;
    private InventoryItem[] armourMenu;

    // Items on amour
    private InventoryItem[] helmetItems;
    private InventoryItem[] shirtItems;
    private InventoryItem[] legsItems;
    private InventoryItem[] shoesItems;

    public MenuManager() {
        mainMenu = new InventoryItem[InventoryType.CHEST.getDefaultSize() + 18];
        armourMenu = new InventoryItem[InventoryType.CHEST.getDefaultSize()];
    }

    public void buildInventoryMenus() {
        ItemStack mainMenuIS = InventoryAPI.createItemStack(Material.CHEST, 1, (short) 0, ChatColor.GOLD + "" + ChatColor.BOLD + "Vanity", null, true);
        ItemStack armourMenuIS = InventoryAPI.createItemStack(Material.IRON_CHESTPLATE, 1, (short) 0, ChatColor.GOLD + "Set Clothing", null, true);

        ItemMeta meta = mainMenuIS.getItemMeta();
        meta.setDisplayName(FontFormat.translateString("&e&lLOOT"));
        mainMenuIS.setItemMeta(meta);

        mainMenu[19] = MenuItems.HAT.getItem();
        mainMenu[12] = MenuItems.EFFECT.getItem();
        mainMenu[14] = MenuItems.TRAIL.getItem();

        armourMenu[0] = MenuItems.HEAD.getItem();
        armourMenu[1] = MenuItems.CHEST.getItem();
        armourMenu[2] = MenuItems.LEGS.getItem();
        armourMenu[3] = MenuItems.BOOTS.getItem();

        mainMenu[25] = MenuItems.PET.getItem();
        mainMenu[31] = MenuItems.GADGET.getItem();

        SubMenuInvokingItemStack apiItemStack = new SubMenuInvokingItemStack(mainMenuIS, InventoryType.CHEST.getDefaultSize() + 18, mainMenu, "Select a category:");

        try {
            InventoryAPI.addGlobalInventoryItem(apiItemStack, 5, 4);
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