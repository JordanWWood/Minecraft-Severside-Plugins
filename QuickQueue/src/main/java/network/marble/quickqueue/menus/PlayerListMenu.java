package network.marble.quickqueue.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.StandardItemStack;
import network.marble.quickqueue.parties.InviteList;
import network.marble.quickqueue.parties.Party;

public class PlayerListMenu extends Menu{
    public ActionExecutor executor;
    ArrayList<InventoryItem> items = new ArrayList<>();
    ArrayList<ItemStack> realItems = new ArrayList<>();

    /*
     * Specifically for showing a player's party invites
     */
    public PlayerListMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, InviteList invites, ActionExecutor executor, boolean requiresConfirmation, String title, boolean closeOnClick, String lorePhrase) {
        this(targetPlayer, inventoryItem, inventorySize, new ArrayList<>(), executor, requiresConfirmation, title, true, closeOnClick, lorePhrase);//TODO fix
    }

    public PlayerListMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, final Party party, ActionExecutor executor, boolean requiresConfirmation, String titlePhrase, boolean excludeViewer, boolean closeOnClick, String lorePhrase) {
        this(targetPlayer, inventoryItem, inventorySize, party.getMembersWithLeader(), executor, requiresConfirmation, titlePhrase, excludeViewer, closeOnClick, lorePhrase);
    }

    public PlayerListMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, final Collection<UUID> players, ActionExecutor executor, boolean requiresConfirmation, String titlePhrase, boolean excludeViewer, boolean closeOnClick, String lorePhrase) {
        super(targetPlayer, inventoryItem, inventorySize);
        this.executor = executor;

        targetPlayer.openInventory(Bukkit.createInventory(null, 9, Lang.get("qq.menu.loading", targetPlayer)));

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            final String lore = Lang.get(lorePhrase, targetPlayer);

            for(UUID m : players){
                try {
                    Player p = Bukkit.getPlayer(m);
                    String username = p != null ? p.getName() : new User().get(m).displayName;
                    //Skip showing the player viewing the menu
                    if(excludeViewer && m.equals(targetPlayer.getUniqueId())) continue;
                    ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
                    //Create meta and set skull to look like player
                    SkullMeta meta = (SkullMeta)stack.getItemMeta();
                    meta.setOwner(username);//TODO get name
                    meta.setLore(Collections.singletonList(lore));
                    //Display user name as item name
                    meta.setDisplayName(username);//TODO get name

                    //Apply meta
                    stack.setItemMeta(meta);

                    items.add(executor == null ? new StandardItemStack(stack) : new ActionItemStack(stack, executor, requiresConfirmation, new String[]{username, m.toString()}).setCloseOnExecute(closeOnClick));
                    realItems.add(stack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int size = players.size() > 0 ? players.size()/9 + (players.size() % 9 != 0 ? 1 : 0) : 1;//Calculate rows
            size *= 9;//Times the rows into slots
            Inventory inv = Bukkit.createInventory(targetPlayer, size, Lang.get(titlePhrase, targetPlayer));
            ItemStack[] i = new ItemStack[realItems.size()];
            inv.setContents(realItems.toArray(i));
            Bukkit.getScheduler().runTask(QuickQueue.getInstance(), () -> {
                if(InventoryAPI.getPlayerCurrentMenu(targetPlayer.getUniqueId()) == this) {
                    targetPlayer.openInventory(inv);
                    InventoryAPIPlugin.playerCurrentMenus.put(targetPlayer.getUniqueId(), this);//Bad
                }
            });
        });
    }

    @Override
    public boolean execute(int slot, int rawSlot) {
        if(rawSlot < items.size()) {
            Bukkit.getLogger().info("items " + items);
            if(items.get(rawSlot)!=null) items.get(rawSlot).execute(getTargetPlayer(), rawSlot);
        }
        return false;
    }
}
