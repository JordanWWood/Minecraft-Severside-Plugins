package network.marble.quickqueue.menus.getters;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.menus.Menus;

public class ManagePartyGetter implements ItemStackGetter{

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        return InventoryAPI.renameItemstack(Menus.managePartyIS, Lang.get("qq.parties.tag", player));
    }
}
