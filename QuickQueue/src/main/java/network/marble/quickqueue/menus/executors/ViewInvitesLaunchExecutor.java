package network.marble.quickqueue.menus.executors;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.actions.AcceptInvite;
import network.marble.quickqueue.menus.PlayerListMenu;

public class ViewInvitesLaunchExecutor implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem itemTriggered, String[] args) {
//        InventoryAPI.openMenuForPlayer(p.getUniqueId(), new PlayerListMenu(p, itemTriggered, 54, QuickQueue.invites.get(p.getUniqueId()), new AcceptInvite(), true, "Party Invites", true));
    }
}
