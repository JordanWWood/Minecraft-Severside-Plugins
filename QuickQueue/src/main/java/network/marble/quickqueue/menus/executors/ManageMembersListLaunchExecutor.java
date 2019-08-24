package network.marble.quickqueue.menus.executors;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.quickqueue.actions.RemoveMember;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.menus.PlayerListMenu;
import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ManageMembersListLaunchExecutor implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem itemTriggered, String[] args) {//TODO size scaling
        InventoryAPI.openMenuForPlayer(p.getUniqueId(), new PlayerListMenu(p, itemTriggered, 54, PartyManager.getCachedUserParty(p.getUniqueId()),
                RemoveMember.getInstance(), true, "qq.menu.manage.members.title", true, true, "qq.menu.manage.player.lore.remove"));
    }
}
