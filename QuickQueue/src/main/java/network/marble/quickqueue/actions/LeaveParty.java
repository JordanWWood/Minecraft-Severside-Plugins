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

public class LeaveParty implements ActionExecutor{
    private static LeaveParty instance;

    public static LeaveParty getInstance() {
        if (instance == null) instance = new LeaveParty();
        return instance;
    }

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                Future<PartyResult> result = PartyManager.getInstance().removePlayerFromParty(p.getUniqueId(), p.getName());
                PartyResult res = result.get(3, TimeUnit.SECONDS);
                Lang.chat(res.getResponse().replaceAll("remove", "leave"), p);
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.leave.error", p);
            }
        });
    }
}