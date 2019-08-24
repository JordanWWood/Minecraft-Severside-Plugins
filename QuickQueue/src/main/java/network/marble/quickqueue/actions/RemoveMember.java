package network.marble.quickqueue.actions;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.managers.PartyResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RemoveMember implements ActionExecutor{
    private static RemoveMember instance;

    public static RemoveMember getInstance() {
        if (instance == null) instance = new RemoveMember();
        return instance;
    }

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                Future<PartyResult> result = PartyManager.getInstance().removePlayerFromParty(p.getUniqueId(), args[0]);
                PartyResult res = result.get(3, TimeUnit.SECONDS);
                Lang.chat(res.getResponse(), p);
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.remove.error", p);
            }
        });
    }
}