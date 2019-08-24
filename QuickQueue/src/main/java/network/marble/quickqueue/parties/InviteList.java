package network.marble.quickqueue.parties;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import network.marble.quickqueue.QuickQueue;

public class InviteList {
    @Getter ConcurrentHashMap<UUID, Invite> partyInvites = new ConcurrentHashMap<>();
    UUID player;

    public InviteList(UUID player){
        this.player = player;
        QuickQueue.invites.put(player, this);
    }

    public void clear(){
        partyInvites.clear();
    }

    public Invite getInvite(UUID partyID){
        return partyInvites.get(partyID);
    }

    public void deleteInvite(UUID partyID){
        partyInvites.remove(partyID);
    }

    public boolean addInvite(UUID partyID, Invite invite){
        boolean exists = partyInvites.get(partyID) != null;
        if(!exists) partyInvites.put(partyID, invite);
        return exists;
    }
}
