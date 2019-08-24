package network.marble.quickqueue.menus.executors;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.quickqueue.menus.ManageMemberMenu;
import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

import java.util.UUID;

public class ManageMembersLaunchExecutor implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem itemTriggered, String[] args) {//TODO size scaling
        InventoryAPI.openMenuForPlayer(p.getUniqueId(), new ManageMemberMenu(p, itemTriggered, 54, UUID.fromString(args[0])));
    }
}
