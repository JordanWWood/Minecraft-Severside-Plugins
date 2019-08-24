package network.marble.quickqueue.actions;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class TransferLeadership implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
//        try {
//            UUID sender = p.getUniqueId();
//            User target = new User().getByUUID(UUID.fromString(args[0]));
//            if(QueueAPI.isPlayerOnline(target.uuid)){
//                if(!sender.equals(target)){//check sender is not transferring to themself
//                    Party senderParty = QuickQueue.partyMembers.get(sender).getParty();
//                    if(senderParty != null){//check sender is the leader of a party
//                        Party targetParty = QueueAPI.getMemberPartyID(target.uuid);
//                        if(targetParty.getPartyID().equals(senderParty.getPartyID())){//check target is in same party
//                            try{
//                                senderParty.setLeader(target.uuid);
//                                QueueAPI.applyAppropriateInventoryToPlayer(p);
//                                p.sendMessage(ChatColor.GOLD + "You made " +  ChatColor.GREEN + target.getDisplayName() + ChatColor.GOLD + " the leader of your party.");
//
//                                //new ChangeLeader("", senderParty.getPartyID().toString()).send();//TODO
//                                QueueAPI.messageUUID(target.uuid, ChatColor.GOLD + "You were made party leader!", false);
//                            }catch(Exception e){
//                                e.printStackTrace();
//                                p.sendMessage(ChatColor.RED + "Failed to send invite.");
//                            }
//                        }else{
//                            p.sendMessage(ChatColor.RED + "That player is not in your party.");
//                        }
//                    }else{
//                        p.sendMessage(ChatColor.RED + "You must be a party leader to transfer leadership.");
//                    }
//                }else{
//                    p.sendMessage(ChatColor.RED + "You are already the leader of this party.");
//                }
//            }else{
//                p.sendMessage(ChatColor.RED + "This player is not online.");
//            }
//        }catch (APIException e) {
//            e.printStackTrace();
//        }
        p.sendMessage("Feature under construction.");
    }
}