package network.marble.vanity.menus.getters;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.menus.MenuItems;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VanityIconGetter implements ItemStackGetter {
    private ItemStack itemStack;
    private Long price;

    public VanityIconGetter(ItemStack is, Long price) {
        this.itemStack = is;
        this.price = price;
    }

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        ActionItemStack aic = (ActionItemStack) inventoryItem;
        String name = aic.getExecutorArgs()[0];

        VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(name);

        //TODO Check ownership
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Owned");

        return InventoryAPI.createItemStack(itemStack.getType(), itemStack.getAmount(), itemStack.getDurability(), ChatColor.GOLD + pl.getName(), lore, true);
    }
}
