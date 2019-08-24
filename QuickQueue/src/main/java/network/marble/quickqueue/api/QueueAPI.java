package network.marble.quickqueue.api;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.messageapi.api.MessageAPI;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.menus.PartyMenu;

public class QueueAPI {

    public static void removeBossBarPlayers(UUID... players){
        //TODO BossBarManagerAPI.unregisterPlayers(new ArrayList<>(Arrays.asList(players)), QuickQueue.getInstance().getName());
    }

    public static void applyAppropriateInventoryToPlayer(Player player){//TODO Update from old InventoryAPI central plugin reliance system
        InventoryAPI.refreshPlayerView(player);//TODO refresh appropriate slots
        Menu i = InventoryAPI.getPlayerCurrentMenu(player.getUniqueId());
        if(i instanceof PartyMenu) ((PartyMenu)i).reloadMenu();
    }

    public static void messageUUID(UUID uuid, String message, boolean isCommand){
        if(isCommand){
            //TODO use rabbit to send the message
        }else{
            MessageAPI.sendMessage(uuid, message, false);
        }
    }

    public static boolean checkPlayerIsPriority(UUID playerUUID) throws APIException{
        Rank r = new Rank().get(new User().getByUUID(playerUUID).getRank_id());
        return r.isPriority;
    }

    public static boolean isPlayerOnline(UUID uuid){
        boolean isOnline = false;
        if(Bukkit.getPlayer(uuid) != null){
            isOnline = true;
        }else{
            //TODO hermes
        }
        return isOnline;
    }
    
//    public static boolean isPlayerInParty(UUID uuid){
//        boolean isOnline = false;
//        if(Bukkit.getPlayer(uuid) != null){
//            isOnline = QuickQueue.partyMembers.containsKey(uuid);
//        }else{
//            //TODO hermes
//        }
//        return isOnline;
//    }
    
    public static void addListToQueueBossBars(UUID[] uuids, String gameID){
        ArrayList<Player> onlineMembers = new ArrayList<Player>();
        for(UUID s:uuids){
            UUID uuid = s;
            Player player = QuickQueue.getInstance().getServer().getPlayer(uuid);
            if(player!=null){
                onlineMembers.add(player);
            }else{
                QueueAPI.messageUUID(s, "2:" + s + ":" + gameID, true);
            }
        }
//        BossBarManagerAPI.registerPlayers(onlineMembers, QuickQueue.getInstance().getName(), gameID);
    }
    
    public static void addUUIDToQueueBossBars(UUID uuid, String gameID){//TODO consider revising
        UUID[] arrayUUID = new UUID[1];
        arrayUUID[0] = uuid;
        addListToQueueBossBars(arrayUUID, gameID);
    }
    
//    public static boolean removePlayerFromParty(Member member, UUID partyID){
//        if(member != null){
//            if(member.getParty().getPartyID().equals(partyID)){
//                member.getParty().removeMember(member);
//                QuickQueue.partyMembers.remove(member.getMemberUUID());
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public static boolean removePlayerFromParty(UUID memberID, UUID partyID){
//        Member m = getMember(memberID);
//        return removePlayerFromParty(m, partyID);
//    }
//
//    @Deprecated
//    public static boolean isPlayerAPartyLeader(UUID playerUUID){
//        Member m = getMember(playerUUID);
//        if(m != null){//If the player is a member of any party
//            if(m.getParty() != null && m.getParty().getLeader().equals(playerUUID)){//If the players party isn't mismapped and that their party has them as the leader
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Deprecated
//    public static Member getMember(UUID memberUUID){
//        return QuickQueue.partyMembers.get(memberUUID);
//    }
    
    public static void markPlayerOffline(Player p){
        UUID playerUUID = p.getUniqueId();
        QueueAPI.removeBossBarPlayers(playerUUID);

//        try{
//            Member member = getMember(playerUUID);
//            if(member != null){//Check sender is in a party
//                if(member.isLeader()){//Is sender leader of said party?
//                    if(member.getParty().getMembers().size() == 1){
//                        member.getParty().disbandParty();
//                    }else{
//                        TODO randomly assign another member
//                    }
//                }else{
//                    removePlayerFromParty(playerUUID, member.getParty().getPartyID());
//                }
//            }else{
//                QuickQueue.invites.remove(playerUUID);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

    @Deprecated
    public static boolean playerHasInviteFromParty(UUID uuid, UUID partyID) {
        // TODO Auto-generated method stub
        return false;
    }
}
