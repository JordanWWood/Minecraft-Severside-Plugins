package network.marble.quickqueue.menus;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.inventories.SubMenu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.SubMenuInvokingItemStack;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class LangSubMenuInvokingItemStack extends SubMenuInvokingItemStack {
    public LangSubMenuInvokingItemStack(ItemStack itemStack, InventoryType inventoryType, InventoryItem[] containedItems, String inventoryName) {
        super(itemStack, inventoryType, containedItems, inventoryName);
    }

    public LangSubMenuInvokingItemStack(ItemStack itemStack, InventoryType inventoryType, InventoryItem[] containedItems, String inventoryName, ItemStackGetter getter) {
        super(itemStack, inventoryType, containedItems, inventoryName, getter);
    }

    public LangSubMenuInvokingItemStack(ItemStack itemStack, int inventorySize, InventoryItem[] containedItems, String inventoryName) {
        super(itemStack, inventorySize, containedItems, inventoryName);
    }

    public LangSubMenuInvokingItemStack(ItemStack itemStack, int inventorySize, InventoryItem[] containedItems, String inventoryName, ItemStackGetter getter) {
        super(itemStack, inventorySize, containedItems, inventoryName, getter);
    }

    @Override
    public void execute(final Player p, int slot) {
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            String translatedTitle = Lang.get(this.getInventoryName(), p);
            Bukkit.getScheduler().runTask(QuickQueue.getInstance(), () ->
                    InventoryAPIPlugin.playerCurrentMenus.put(p.getUniqueId(), new SubMenu(p, this, translatedTitle)));
        });
    }
}
