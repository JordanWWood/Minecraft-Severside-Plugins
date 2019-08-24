package network.marble.quickqueue.actions;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class ToggleMemberInvitePrivileges implements ActionExecutor{

    @Override
    public void executeAction(Player p, InventoryItem s, String args[]) {
//        UUID senderUUID = p.getUniqueId();
//        long timeRemainingToReuse = QuickQueue.memberInvitingToggleUsage.get(senderUUID) + (QuickQueue.memberInvitingToggleDelay) - System.currentTimeMillis();
//        if(timeRemainingToReuse <= 0){
//            QuickQueue.memberInvitingToggleUsage.replace(senderUUID, System.currentTimeMillis());
//
//            if(QueueAPI.isPlayerAPartyLeader(senderUUID)){//check sender is the leader of a party
//                Party party = QueueAPI.getMember(senderUUID).getParty();
//                boolean memberInvitingEnabled = !party.isMemberInvitingEnabled();
//                party.setMemberInvitingEnabled(memberInvitingEnabled);
//                for(Member m : party.getMembers()){
//                    UUID uuid = m.getMemberUUID();
//                    if(InventoryAPI.getPlayerCurrentMenu(uuid) instanceof PartyMenu){//Null checking not needed as null will never be an instance of PartyMenu
//                        ((PartyMenu)InventoryAPI.getPlayerCurrentMenu(uuid)).reloadMenu();
//                    }//TODO reload open player list menus of off server of members
//                }
//                //new SetInvitePrivilege("", "").send();//TODO
//                //TODO call specific slot refresh or reload menu to update icon
//                QueueAPI.messageUUID(party.getPartyID(), Strings.MEMBER_INVITE_PRIVILEGE_CHANGED(memberInvitingEnabled), false);
//            }else{
//                p.sendMessage(ChatColor.RED + "You must be a party leader to toggle member invite privileges.");
//            }
//        }else{
//            p.sendMessage(ChatColor.RED + "Please wait another " + (timeRemainingToReuse/1000+1) + " seconds before using this again.");
//        }
        p.sendMessage("Feature under construction.");
    }
}