package network.marble.vanity.menus.getters;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import network.marble.currencyapi.api.CurrencyAPI;
import network.marble.currencyapi.api.KnownCurrency;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.type.base.VanityItemBase;

public class VanityIconGetter implements ItemStackGetter {
    private ItemStack itemStack;

    public VanityIconGetter(ItemStack is) {
        this.itemStack = is;
    }

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        ActionItemStack aic = (ActionItemStack) inventoryItem;
        String name = aic.getExecutorArgs()[0];

        VanityItemBase pl = Vanity.getVanityPluginManager().getPlugins().get(name);

        //TODO Check ownership
        User u = null;
        VanityItem vi = null;
        try {
            u = new User().getByUUID(player.getUniqueId());
            vi = new VanityItem().getByName(pl.getName());
        } catch (APIException e) {
            e.printStackTrace();
        }
        ArrayList<String> lore;
        if(itemStack.getItemMeta().getLore() == null) {
            lore = new ArrayList<>();	
        } else {
            lore = (ArrayList<String>) itemStack.getItemMeta().getLore();
        }
        if (u.getVanityitems().containsKey(vi.getId())) {
            lore.add(ChatColor.GREEN + "Click to Equip");
        } else {
            UUID id = CurrencyAPI.getCurrency(KnownCurrency.CHIPS.getName());
            Long balance = CurrencyAPI.getPlayerBalance(player.getUniqueId(), id);

            if (balance >= vi.getPrice()) {
                lore.add(ChatColor.GREEN + "Click to buy for " + vi.getPrice() + " Chips");
            } else {
                lore.add(ChatColor.RED + "You require " + vi.getPrice() + " Chips to buy this");
            }
        }
        ItemStack is = itemStack.clone();
        ItemMeta meta = is.getItemMeta();
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}
