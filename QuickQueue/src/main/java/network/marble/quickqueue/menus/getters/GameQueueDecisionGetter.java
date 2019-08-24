package network.marble.quickqueue.menus.getters;

import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.parties.Party;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.menus.Menus;

public class GameQueueDecisionGetter implements ItemStackGetter{

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {//TODO non-leader member icon
        Party party = PartyManager.getCachedUserParty(player.getUniqueId());
        boolean inQueue = party != null && party.isQueued();
        return InventoryAPI.renameItemstack(inQueue ? Menus.exitQueueIS : Menus.joinQueueIS, inQueue ? Lang.get("qq.games.leave.tag", player) : Lang.get("qq.games.join.tag", player));
    }
}
