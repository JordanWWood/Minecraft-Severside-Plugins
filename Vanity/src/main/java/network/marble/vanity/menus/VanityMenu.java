package network.marble.vanity.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class VanityMenu extends Menu {
    private final int INVENTORY_SIZE = 54;

    ItemStack[] itemStacks = new ItemStack[INVENTORY_SIZE];
    InventoryItem[] inventoryItems = new InventoryItem[INVENTORY_SIZE];

    Player player;

    public VanityMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, List<InventoryItem> vanityItems, String category) {
        super(targetPlayer, inventoryItem, inventorySize);

        player = targetPlayer;
        for (int i = 0; i < vanityItems.size(); i++) {
            itemStacks[i] = vanityItems.get(i).getItemStack(targetPlayer);
            inventoryItems[i] = vanityItems.get(i);
        }

        Inventory inv = Bukkit.createInventory(targetPlayer, 54, category);
        inv.setContents(itemStacks);

        targetPlayer.openInventory(inv);
    }

    @Override
    public boolean execute(int slot, int rawSlot) {
        if(slot == rawSlot){//Validate the click is in the top of the inventory
            if (inventoryItems[rawSlot] != null) {
                ((ActionItemStack)inventoryItems[rawSlot]).getExecutor().executeAction(player, inventoryItems[rawSlot], null);
            }
            return false;
        }else{
            return true;
        }
    }
}
