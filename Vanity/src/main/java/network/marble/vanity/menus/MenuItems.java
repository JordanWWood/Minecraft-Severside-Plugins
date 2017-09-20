package network.marble.vanity.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.type.base.VanityItemBase;
import network.marble.vanity.menus.executors.VanityCategoryInvokingItemStack;
import network.marble.vanity.menus.executors.VanityItemEquipExecutor;
import network.marble.vanity.menus.getters.VanityIconGetter;


public enum MenuItems {
    HAT("Hat", InventoryAPI.createItemStack(Material.SKULL_ITEM, 1, (short) 0, ChatColor.GOLD + "Set Hat", null, true), Slot.HEAD),
    TRAIL("Trail", InventoryAPI.createItemStack(Material.BLAZE_ROD, 1, (short) 0, ChatColor.GOLD + "Set Trail", null, true), Slot.TRAIL),
    EFFECT("Effect", InventoryAPI.createItemStack(Material.BLAZE_POWDER, 1, (short) 0, ChatColor.GOLD + "Set Effects", null, true), Slot.EFFECT),

    HEAD("Head", InventoryAPI.createItemStack(Material.CHAINMAIL_HELMET, 1, (short) 0, ChatColor.GOLD + "Set Helmet", null, true), Slot.HEAD),
    CHEST("Chest", InventoryAPI.createItemStack(Material.CHAINMAIL_CHESTPLATE, 1, (short) 0, ChatColor.GOLD + "Set Chest", null, true), Slot.CHEST),
    LEGS("Legs", InventoryAPI.createItemStack(Material.CHAINMAIL_LEGGINGS, 1, (short) 0, ChatColor.GOLD + "Set Legs", null, true), Slot.LEGS),
    BOOTS("Boots", InventoryAPI.createItemStack(Material.CHAINMAIL_BOOTS, 1, (short) 0, ChatColor.GOLD + "Set Boots", null, true), Slot.BOOTS);

    @Getter ActionItemStack item;
    @Getter String simpleName;
    @Getter Slot slot;

    @Getter List<InventoryItem> items = new ArrayList<>();

    MenuItems(String simpleName, ItemStack item, Slot slot) {
        this.item = new ActionItemStack(item, new VanityCategoryInvokingItemStack(), false, new String[]{simpleName});
        this.simpleName = simpleName;

        List<VanityItem> temp = new ArrayList<>();
        try {
            temp = new VanityItem().getByCategory(simpleName);
        } catch (APIException e) {
            e.printStackTrace();
        }

        if (temp.size() > 0) {
            for (VanityItem vi : temp) {
                VanityItemBase pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());

                if (pl != null) {
                    ItemStack is = InventoryAPI.mergeItemMeta(pl.getDisplayItem(), true);
                    ItemMeta meta = is.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + pl.getName());
                    is.setItemMeta(meta);
                    items.add(new ActionItemStack(is, new VanityItemEquipExecutor(pl), false, new VanityIconGetter(is), new String[]{pl.getName()}));
                }
            }
        }
    }
}
