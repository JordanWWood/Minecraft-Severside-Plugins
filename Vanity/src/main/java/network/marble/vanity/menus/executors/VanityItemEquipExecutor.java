package network.marble.vanity.menus.executors;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import network.marble.currencyapi.api.CurrencyAPI;
import network.marble.currencyapi.api.KnownCurrency;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.ConfirmationMenu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.api.type.base.VanityItemBase;
import network.marble.vanity.managers.EquipmentManager;

public class VanityItemEquipExecutor implements ActionExecutor {
    VanityItemBase pl;

    public VanityItemEquipExecutor(VanityItemBase pl) {
        this.pl = pl;
    }

    @Override
    public void executeAction(Player player, InventoryItem inventoryItem, String[] strings) {
        VanityItem vi = null;
        User u = null;

        try {
            vi = new VanityItem().getByName(pl.getName());
            u = new User().getByUUID(player.getUniqueId());
        } catch (APIException e) {
            e.printStackTrace();
            return;
        }
        
        if (u.getVanityitems().containsKey(vi.getId())) {
        	if (!EquipmentManager.getPlayerEquipment().containsKey(player.getUniqueId())) {
    			EquipmentManager.getPlayerEquipment().put(player.getUniqueId(), new HashMap<>());
    			EquipmentManager.getPlayerEquipmentBySlot().put(player.getUniqueId(), new HashMap<>());
    		}
        	if(EquipmentManager.getPlayerEquipment().get(player.getUniqueId()).containsKey(pl.getName())) {
        		pl.unEquip(player);
                pl.unEquipWriteTo(player);
        	} else {
        		pl.equip(player);
        		pl.equipWriteTo(player);
        	}
        } else {
            player.sendMessage(ChatColor.RED + "You do not own that item!");

            UUID id = CurrencyAPI.getCurrency(KnownCurrency.CHIPS.getName());
            Long balance = CurrencyAPI.getPlayerBalance(player.getUniqueId(), id);

            if (balance >= vi.price) {
                ConfirmationMenu menu = new ConfirmationMenu(player, null, new VanityPurchaseExecutor(vi),
                        "Agree to spend " + vi.getPrice() + " to purchase " + vi.getName(), "Close menu");
                InventoryAPI.openMenuForPlayer(player.getUniqueId(), menu);
            } else {
                player.sendMessage(ChatColor.RED + "and you do not have enough it");
            }
        }
    }
}
