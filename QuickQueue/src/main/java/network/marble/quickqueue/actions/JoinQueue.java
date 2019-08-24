package network.marble.quickqueue.actions;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.managers.PartyResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class JoinQueue implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
        final UUID senderUUID = p.getUniqueId();
        final UUID clickedID = UUID.fromString(args[0]);

        Future<PartyResult> resultFuture = PartyManager.getInstance().joinAtlas(clickedID, senderUUID);

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                PartyResult res = resultFuture.get(3, TimeUnit.SECONDS);
                Lang.chat(res.getResponse(), p);
                if(res.isSuccess()) InventoryAPI.refreshPlayerView(p);
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.join.queue.error", p);
            }
        });
    }
}