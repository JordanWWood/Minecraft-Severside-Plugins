package network.marble.quickqueue.actions;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.managers.PartyResult;
import network.marble.quickqueue.menus.PartyMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class CreateParty implements ActionExecutor {
    private static CreateParty instance;

    public static CreateParty getInstance() {
        if (instance == null) instance = new CreateParty();
        return instance;
    }

    @Override
    public void executeAction(Player player, InventoryItem itemTriggered, String[] args) {
        final Player p = player;
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                Future<PartyResult> result = PartyManager.getInstance().createParty(p.getUniqueId());
                PartyResult res = result.get(3, TimeUnit.SECONDS);
                Lang.chat(res.getResponse(), p);
                if (res.isSuccess()) {
                    Menu m = InventoryAPI.getPlayerCurrentMenu(p.getUniqueId());
                    if (m instanceof PartyMenu) {
                        ((PartyMenu) m).reloadMenu();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.error.create.party", p);
            }
        });
    }
}