package network.marble.vanity.menus.executors;

import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.menus.MenuItems;
import network.marble.vanity.menus.VanityMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class VanityCategoryInvokingItemStack implements ActionExecutor {
    @Override
    public void executeAction(Player player, InventoryItem inventoryItem, String[] strings) {
        List<InventoryItem> items = MenuItems.valueOf(strings[0].toUpperCase()).getItems();
        if (items.size() == 0) {
            player.sendMessage("No vanity items could be found for this category");
            return;
        }

        for (InventoryItem item : items) {
            Vanity.getInstance().getLogger().info(item.toString());
        }

        InventoryAPI.openMenuForPlayer(player.getUniqueId(), new VanityMenu(player, inventoryItem, InventoryType.CHEST.getDefaultSize() * 2, items));
    }
}
