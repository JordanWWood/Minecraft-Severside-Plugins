package network.marble.vanity.menus.executors;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import network.marble.vanity.api.Slot;
import network.marble.vanity.menus.MenuItems;
import network.marble.vanity.menus.VanityMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class VanityCategoryInvokingItemStack implements ActionExecutor {
    private Slot slot;

    public VanityCategoryInvokingItemStack(Slot slot) {
        this.slot = slot;
    }

    @Override
    public void executeAction(Player player, InventoryItem inventoryItem, String[] strings) {
        List<InventoryItem> items = MenuItems.valueOf(strings[0].toUpperCase()).getItems();
        if (items.size() == 0) {
            player.sendMessage(Lang.get("van.message." + strings[0].toLowerCase() + ".noitems", player));
            return;
        }

        InventoryAPI.openMenuForPlayer(player.getUniqueId(), new VanityMenu(player, inventoryItem, InventoryType.CHEST.getDefaultSize() * 2, items,
                Lang.get("van.menu.name." + strings[0].toLowerCase(), player)));
    }
}
