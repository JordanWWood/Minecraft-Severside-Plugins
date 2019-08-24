package network.marble.quickqueue.menus.executors;

import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.parties.Party;
import org.bukkit.entity.Player;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.ConfirmationMenu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.SubMenuInvokingItemStack;
import network.marble.quickqueue.actions.LeaveQueue;

public class GameQueueDecisionExecutor implements ActionExecutor{
    SubMenuInvokingItemStack gameSelector;

    public GameQueueDecisionExecutor(SubMenuInvokingItemStack gameSelector){
        this.gameSelector = gameSelector;
    }

    @Override
    public void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String[] args){
        Party party = PartyManager.getCachedUserParty(triggeringPlayer.getUniqueId());
        boolean inQueue = party != null && party.isQueued();

        if(!inQueue) gameSelector.execute(triggeringPlayer, 5);
        else InventoryAPI.openMenuForPlayer(triggeringPlayer.getUniqueId(), new ConfirmationMenu(triggeringPlayer, itemTriggered, new LeaveQueue(), "Remove your party from the game queue.", "Remain queued for the games queue.", "Leave the game queue?"));//TODO put the currently queued game name in
    }
}
