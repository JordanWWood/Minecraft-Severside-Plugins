package network.marble.quickqueue.actions;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messageapi.api.MessageAPI;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.managers.PartyResult;
import network.marble.quickqueue.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AcceptInvite implements ActionExecutor{
    private static AcceptInvite instance;

    public static AcceptInvite getInstance() {
        if (instance == null) instance = new AcceptInvite();
        return instance;
    }

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                Future<PartyResult> result = PartyManager.getInstance().joinParty(p.getUniqueId(), args[0]);
                PartyResult res = result.get(3, TimeUnit.SECONDS);
                Lang.chat(res.getResponse(), p);
                Party party = PartyManager.getInstance().getUserPartySync(p.getUniqueId());
                for(UUID member : party.getMembersWithLeader()){
                    if(member.equals(p.getUniqueId())) continue;
                    String message = Lang.replaceUnparsedTag(Lang.get("qq.success.join.notify", member), "player.name", p.getName());
                    MessageAPI.sendMessage(p.getUniqueId(), member, message, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.error", p);
            }
        });


        //TODO have player leave old party first
//        InventoryAPI.setPlayerInventory(p, Menus.getMemberMenuID());
//        p.sendMessage(ChatColor.RED + "You do not have an invite to that party.");
//        p.sendMessage(ChatColor.RED + "You cannot accept invites whilst in a party.");
    }
}
