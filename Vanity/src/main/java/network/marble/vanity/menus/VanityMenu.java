package network.marble.vanity.menus;

import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.managers.VanityPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class VanityMenu extends Menu {
    private final int INVENTORY_SIZE = 54;

    ItemStack[] itemStacks = new ItemStack[INVENTORY_SIZE];
    InventoryItem[] inventoryItems = new InventoryItem[INVENTORY_SIZE];

    Player player;

    public VanityMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, List<InventoryItem> vanityItems) {
        super(targetPlayer, inventoryItem, inventorySize);

        player = targetPlayer;
        Vanity.getInstance().getLogger().info(vanityItems.size() + "");
        for (int i = 0; i < vanityItems.size(); i++) {
            Vanity.getInstance().getLogger().info("Added: " + vanityItems.get(i).getItemStack(targetPlayer).getType());
            itemStacks[i] = vanityItems.get(i).getItemStack(targetPlayer);
            inventoryItems[i] = vanityItems.get(i);
        }

        Inventory inv = Bukkit.createInventory(targetPlayer, 54, "Category");
        Vanity.getInstance().getLogger().info(inv.getName() + "|" + inv.getTitle());
        inv.setContents(itemStacks);

        targetPlayer.openInventory(inv);
    }

    @Override
    public boolean execute(int slot, int rawSlot) {
        if (inventoryItems[rawSlot] != null) {
            ((ActionItemStack)inventoryItems[rawSlot]).getExecutor().executeAction(player, inventoryItems[rawSlot], null);
            return true;
        }

        return false;
    }
}
