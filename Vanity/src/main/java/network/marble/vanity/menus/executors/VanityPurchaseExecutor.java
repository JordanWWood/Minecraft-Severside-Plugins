package network.marble.vanity.menus.executors;

import network.marble.currencyapi.api.CurrencyAPI;
import network.marble.currencyapi.api.KnownCurrency;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VanityPurchaseExecutor implements ActionExecutor {
    private VanityItem item;
    private VanityPlugin pl;

    VanityPurchaseExecutor(VanityItem item, VanityPlugin pl) {
        this.item = item;
        this.pl = pl;
    }

    @Override
    public void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            UUID id = CurrencyAPI.getCurrency(KnownCurrency.TICKETS.getName());
            CurrencyAPI.takeCurrency(triggeringPlayer.getUniqueId(), id, item.getPrice(), "Purchase " + item.getName());

            try {
                User u = new User().get(triggeringPlayer.getUniqueId());
                u.getVanityitems().put(item.getId(), 1);
                u.save();
                pl.onEquip(triggeringPlayer);
            } catch (APIException e) {
                e.printStackTrace();
            }
        });
    }
}
