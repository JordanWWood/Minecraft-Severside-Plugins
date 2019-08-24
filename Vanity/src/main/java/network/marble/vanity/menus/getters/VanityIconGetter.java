package network.marble.vanity.menus.getters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import network.marble.messagelibrary.api.Lang;
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
import network.marble.vanity.api.base.VanityPlugin;

public class VanityIconGetter implements ItemStackGetter {
    private ItemStack itemStack;

    public VanityIconGetter(ItemStack is) {
        this.itemStack = is;
    }

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        ActionItemStack aic = (ActionItemStack) inventoryItem;
        String name = aic.getExecutorArgs()[0];

        VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(name);

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
            List<String> lines = new ArrayList<String>();
            lore.addAll(Arrays.asList(Lang.get("van.owned.lore", player).split("\\\\n")));
        } else {
            UUID id = CurrencyAPI.getCurrency(KnownCurrency.TICKETS.getName());
            long balance = CurrencyAPI.getPlayerBalance(player.getUniqueId(), id);

            if (vi.getPrice() == -1) {
                String s = Lang.get("van.buy.lore.preq.premium", player);

                lore.addAll(Arrays.asList(s.split("\\\\n")));
            } else if (balance >= vi.getPrice()) {
                String s = Lang.get("van.buy.lore.preq.met", player);
                s = Lang.replaceUnparsedTag(s, "price", vi.getPrice() + "");

                lore.addAll(Arrays.asList(s.split("\\\\n")));
            } else {
                String s = Lang.get("van.buy.lore.preq.fail", player);
                s = Lang.replaceUnparsedTag(s, "price", vi.getPrice() + "");

                lore.addAll(Arrays.asList(s.split("\\\\n")));
            }
        }
        ItemStack is = itemStack.clone();
        ItemMeta meta = is.getItemMeta();
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}
