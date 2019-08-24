package network.marble.quickqueue.menus;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ManageMemberMenu extends Menu{
    public ActionExecutor executor;
    ArrayList<InventoryItem> items = new ArrayList<>();
    UUID m;

    public ManageMemberMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, UUID m) {
        super(targetPlayer, inventoryItem, InventoryType.HOPPER.getDefaultSize());
        this.m = m;

        Inventory inv = Bukkit.createInventory(targetPlayer, InventoryType.HOPPER, "Manage Members - " + m);//TODO get name
        ItemStack[] i = new ItemStack[InventoryType.HOPPER.getDefaultSize()];
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
        //Create meta and set skull to look like player
        SkullMeta meta = (SkullMeta)head.getItemMeta();
//        meta.setOwner(m.getUserName());//TODO get name
        //Display user name as item name
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + m);//TODO get name
        //Apply meta
        head.setItemMeta(meta);
        ItemStack spacer = Menus.spacer.getItemStack(targetPlayer);
        i[0] = Menus.leaderOnly.getItemStack(targetPlayer);
        i[1] = spacer;
        i[2] = head;
        i[3] = spacer;
        i[4] = Menus.leaderOnly.getItemStack(targetPlayer);

        inv.setContents(i);
        targetPlayer.openInventory(inv);
    }

    @Override
    public boolean execute(int slot, int rawSlot) {
        if(rawSlot < items.size()){
            if(items.get(rawSlot)!=null) items.get(rawSlot).execute(getTargetPlayer(), rawSlot);
        }
        return false;
    }
}
