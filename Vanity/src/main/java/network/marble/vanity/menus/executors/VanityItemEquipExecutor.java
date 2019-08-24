package network.marble.vanity.menus.executors;

import network.marble.currencyapi.api.CurrencyAPI;
import network.marble.currencyapi.api.KnownCurrency;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.ConfirmationMenu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.managers.EquipmentManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class VanityItemEquipExecutor implements ActionExecutor {
    VanityPlugin pl;

    public VanityItemEquipExecutor(VanityPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void executeAction(Player player, InventoryItem inventoryItem, String[] strings) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            VanityItem vi = null;
            User u = null;

            try {
                vi = new VanityItem().getByName(pl.getName());
                u = new User().get(player.getUniqueId());
            } catch (APIException e) {
                e.printStackTrace();
                return;
            }

            if (u.getVanityitems().containsKey(vi.getId())) {
                if (!EquipmentManager.getPlayerEquipment().containsKey(player.getUniqueId())) {
                    EquipmentManager.getPlayerEquipment().put(player.getUniqueId(), new HashMap<>());
                    EquipmentManager.getPlayerEquipmentBySlot().put(player.getUniqueId(), new HashMap<>());
                }
                if (EquipmentManager.getPlayerEquipment().get(player.getUniqueId()).containsKey(pl.getName())) {
                    pl.onRemove(player);
                } else {
                    pl.onEquip(player);
                }
            } else {
                if (vi.price == -1) {
                    String[] lines = Lang.get("van.msg.premium", player).split("\\\\n");
                    player.sendMessage(lines);
                    return;
                }

                String[] lines = Lang.get("van.msg.ownership", player).split("\\\\n");
                player.sendMessage(lines);

                UUID id = CurrencyAPI.getCurrency(KnownCurrency.TICKETS.getName());
                long balance = CurrencyAPI.getPlayerBalance(player.getUniqueId(), id);

                if (balance >= vi.price) {
                    ConfirmationMenu menu = new ConfirmationMenu(player, null, new VanityPurchaseExecutor(vi, pl),
                            "Agree to spend " + vi.getPrice() + " to purchase " + vi.getName(), "Close menu");
                    InventoryAPI.openMenuForPlayer(player.getUniqueId(), menu);
                } else {
                    player.sendMessage(ChatColor.RED + "and you do not have enough it");
                }
            }
        });
    }
}
