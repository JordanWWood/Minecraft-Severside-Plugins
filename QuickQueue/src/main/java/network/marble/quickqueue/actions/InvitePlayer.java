package network.marble.quickqueue.actions;

import network.marble.dataaccesslayer.models.user.User;
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

public class InvitePlayer implements ActionExecutor{
    private static InvitePlayer instance;

    public static InvitePlayer getInstance() {
        if (instance == null) instance = new InvitePlayer();
        return instance;
    }

    @Override
    public void executeAction(Player player, InventoryItem s, String args[]) {
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                User u = new User().getByUsername(args[0].toLowerCase());//TODO check if this is needed by seeing if arg is valid uuid
                if (u.exists()) {
                    Future<PartyResult> result = PartyManager.getInstance().invitePlayer(player, u.getId());
                    PartyResult res = result.get(3, TimeUnit.SECONDS);
                    Lang.chat(res.getResponse(), player);
                }else{
                    Lang.chat("qq.error.invite.player.nonexistent", player);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("error", player);
            }
        });



//        QueueAPI.messageUUID(receiver.uuid, ChatColor.GOLD + "You recieved an invite to join " + ChatColor.GREEN + p.getDisplayName() + ChatColor.GOLD + "'s party!", false);
//        p.sendMessage(ChatColor.RED + "The player you are trying to invite is already in a party.");
    }
}