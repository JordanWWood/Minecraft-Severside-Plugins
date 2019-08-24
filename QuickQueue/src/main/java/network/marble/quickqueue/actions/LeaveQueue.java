package network.marble.quickqueue.actions;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.managers.PartyResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LeaveQueue implements ActionExecutor{
    private static LeaveQueue instance;

    public static LeaveQueue getInstance() {
        if (instance == null) instance = new LeaveQueue();
        return instance;
    }

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
        final UUID senderUUID = p.getUniqueId();

        Future<PartyResult> resultFuture = PartyManager.getInstance().leaveAtlas(senderUUID);

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                PartyResult res = resultFuture.get(3, TimeUnit.SECONDS);
                Lang.chat(res.getResponse(), p);
                if(res.isSuccess()) InventoryAPI.refreshPlayerView(p);
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.join.dequeue.error", p);
            }
        });
    }
}