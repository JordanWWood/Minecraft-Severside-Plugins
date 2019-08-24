package network.marble.quickqueue.menus.executors;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.menus.PlayerListMenu;
import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ViewMembersLaunchExecutor implements ActionExecutor{
    private boolean showLauncher;
    public ViewMembersLaunchExecutor(boolean showLauncher){
        this.showLauncher = showLauncher;
    }

    @Override
    public void executeAction(Player p, InventoryItem itemTriggered, String[] args) {//TODO size scaling
        InventoryAPI.openMenuForPlayer(p.getUniqueId(), new PlayerListMenu(p, itemTriggered, 54, PartyManager.getCachedUserParty(p.getUniqueId()), null, true, "qq.menu.view.members.title", showLauncher, false, "qq.menu.manage.player.lore.partied"));
    }
}
