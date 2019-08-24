package network.marble.quickqueue.menus.executors;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.quickqueue.menus.PartyMenu;
import org.bukkit.entity.Player;

public class PartyMenuLaunchExecutor implements ActionExecutor{

    @Override
    public void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String[] args) {
        InventoryAPI.openMenuForPlayer(triggeringPlayer.getUniqueId(), new PartyMenu(triggeringPlayer, itemTriggered, 5));
    }
}
