package network.marble.vanity.menus;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.ItemInformation;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messageapi.api.FontFormat;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.error.InsufficientDataException;
import network.marble.vanity.error.VersionNotFoundException;
import network.marble.vanity.internal.DatabaseItem;
import network.marble.vanity.menus.executors.VanityCategoryInvokingItemStack;
import network.marble.vanity.menus.executors.VanityItemEquipExecutor;
import network.marble.vanity.menus.getters.VanityIconGetter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public enum MenuItems {
    HAT("Hat", InventoryAPI.createItemStack(Material.SKULL_ITEM, 1, (short) 0, ChatColor.GOLD + "Set Hat", null, true), Slot.HEAD),
    TRAIL("Trail", InventoryAPI.createItemStack(Material.BLAZE_ROD, 1, (short) 0, ChatColor.GOLD + "Set Trail", null, true), Slot.TRAIL),
    EFFECT("Effect", InventoryAPI.createItemStack(Material.BLAZE_POWDER, 1, (short) 0, ChatColor.GOLD + "Set Effects", null, true), Slot.EFFECT),
    GADGET("Gadget", InventoryAPI.createItemStack(Material.REDSTONE, 1, (short) 0, ChatColor.GOLD + "Set Gadgets", null, true), Slot.GADGET),
    HEAD("Head", InventoryAPI.createItemStack(Material.CHAINMAIL_HELMET, 1, (short) 0, ChatColor.GOLD + "Set Helmet", null, true), Slot.HEAD),
    CHEST("Chest", InventoryAPI.createItemStack(Material.CHAINMAIL_CHESTPLATE, 1, (short) 0, ChatColor.GOLD + "Set Chest", null, true), Slot.CHEST),
    LEGS("Legs", InventoryAPI.createItemStack(Material.CHAINMAIL_LEGGINGS, 1, (short) 0, ChatColor.GOLD + "Set Legs", null, true), Slot.LEGS),
    BOOTS("Boots", InventoryAPI.createItemStack(Material.CHAINMAIL_BOOTS, 1, (short) 0, ChatColor.GOLD + "Set Boots", null, true), Slot.BOOTS),
    PET("Pet", InventoryAPI.createItemStack(Material.MONSTER_EGG, 1, (short) 93, ChatColor.GOLD + "Set Pet", null, true), Slot.COMPANION);

    @Getter private ActionItemStack item;
    @Getter private String simpleName;
    @Getter private Slot slot;

    @Getter
    List<InventoryItem> items = new ArrayList<>();

    MenuItems(String simpleName, ItemStack item, Slot slot) {
        ItemMeta ismeta = item.getItemMeta();
        ismeta.setDisplayName(FontFormat.translateString("&6&l" + (simpleName.endsWith("s") ? simpleName : simpleName + "s") ));
        item.setItemMeta(ismeta);

        this.item = new ActionItemStack(item, new VanityCategoryInvokingItemStack(slot), false, new String[]{simpleName});
        this.simpleName = simpleName;

        List<VanityItem> temp = new ArrayList<>();
        try {
            temp = new VanityItem().getByCategory(simpleName);
        } catch (APIException e) {
            e.printStackTrace();
        }

        if (temp.size() > 0) {
            for (VanityItem vi : temp) {
                switch (vi.type) {
                    case PLUGIN: {
                        VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());

                        if (pl == null)
                            continue;

                        ItemStack is = InventoryAPI.mergeItemMeta(pl.getItemStack(), true);
                        ItemMeta meta = is.getItemMeta();
                        meta.setDisplayName(ChatColor.GOLD + pl.getName());
                        is.setItemMeta(meta);
                        items.add(new ActionItemStack(is, new VanityItemEquipExecutor(pl), false, new VanityIconGetter(is), new String[]{pl.getName()}));
                    } break;

                    case PURE: {
                        String a = Vanity.getInstance().getServer().getClass().getPackage().getName();
                        String version = a.substring(a.lastIndexOf('.') + 1);
                        Vanity.getInstance().getLogger().info("Vanity detected the current version as " + version);

                        ItemInformation itemInformation = vi.getItemInformation().get(version);
                        if (itemInformation == null) {
                            try {
                                throw new VersionNotFoundException(version, vi);
                            } catch (VersionNotFoundException e) {
                                e.printStackTrace();
                            }

                            continue;
                        }

                        Material mat = Material.valueOf(itemInformation.getMaterial());

                        ItemStack is = new ItemStack(mat, 1);
                        if (itemInformation.getData() != null) {
                            short s = (short) Short.valueOf(itemInformation.getData());
                            is = new ItemStack(mat, 1, s);
                        }

                        if (mat == Material.SKULL_ITEM) {
                            if (itemInformation.getSkinTexture() == null) {
                                try {
                                    throw new InsufficientDataException(vi, "SkinTexture", version);
                                } catch (InsufficientDataException e) {
                                    e.printStackTrace();
                                }

                                continue;
                            }

                            is = InventoryAPI.setSkullTexture(itemInformation.getSkinTexture());
                        }

                        ItemMeta itemMeta = is.getItemMeta();

                        itemMeta.setDisplayName(vi.getName());
                        is.setItemMeta(itemMeta);

                        DatabaseItem dbItem = new DatabaseItem(vi.getName(), is, Slot.valueOf(vi.getSlot()));

                        Vanity.getVanityPluginManager().getPlugins().putIfAbsent(dbItem.getName(), dbItem);
                        items.add(new ActionItemStack(is, new VanityItemEquipExecutor(dbItem), false, new VanityIconGetter(is), new String[]{dbItem.getName()}));
                    } break;

                    case HYBRID: {
                        VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());

                        String a = Vanity.getInstance().getServer().getClass().getPackage().getName();
                        String version = a.substring(a.lastIndexOf('.') + 1);

                        if (pl == null) {
                            try {
                                throw new InsufficientDataException(vi, "Plugin could not be found for Hybrid Vanity item: ", version);
                            } catch (InsufficientDataException e) {
                                e.printStackTrace();
                            }

                            continue;
                        }

                        ItemInformation itemInformation = vi.getItemInformation().get(version);
                        if (itemInformation == null) {
                            try {
                                throw new VersionNotFoundException(version, vi);
                            } catch (VersionNotFoundException e) {
                                e.printStackTrace();
                            }

                            continue;
                        }


                    } break;

                    default: {
                        try {
                            throw new InsufficientDataException(vi, "type", vi.getType() == null ? "null" : vi.getType().name());
                        } catch (InsufficientDataException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
