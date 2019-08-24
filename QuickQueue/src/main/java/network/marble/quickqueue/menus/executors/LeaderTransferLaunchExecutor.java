package network.marble.quickqueue.menus.executors;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class LeaderTransferLaunchExecutor implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem itemTriggered, String[] args) {
//        InventoryAPI.openMenuForPlayer(p.getUniqueId(), new PlayerListMenu(p, itemTriggered, 54, QueueAPI.getMemberPartyID(p.getUniqueId()), new TransferLeadership(), true, "Transfer Party Leadership", true, true));
        p.sendMessage("Feature under construction.");
    }
}
