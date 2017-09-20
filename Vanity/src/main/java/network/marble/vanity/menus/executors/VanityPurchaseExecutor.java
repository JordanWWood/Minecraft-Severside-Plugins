package network.marble.vanity.menus.executors;

import java.util.UUID;

import org.bukkit.entity.Player;

import network.marble.currencyapi.api.CurrencyAPI;
import network.marble.currencyapi.api.KnownCurrency;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;

public class VanityPurchaseExecutor implements ActionExecutor {
    private VanityItem item;

    public VanityPurchaseExecutor(VanityItem item) {
        this.item = item;
    }

    @Override
    public void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String[] args) {
        UUID id = CurrencyAPI.getCurrency(KnownCurrency.CHIPS.getName());
        CurrencyAPI.takeCurrency(triggeringPlayer.getUniqueId(), id, item.getPrice(), "Purchase " + item.getName());

        try {
            User u = new User().getByUUID(triggeringPlayer.getUniqueId());
            u.getVanityitems().put(item.getId(), 1);
            u.save();
            Vanity.getVanityPluginManager().getPlugins().get(item.getName()).equip(triggeringPlayer);
            Vanity.getVanityPluginManager().getPlugins().get(item.getName()).equipWriteTo(triggeringPlayer);
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
