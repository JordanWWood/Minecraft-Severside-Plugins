package network.marble.quickqueue.messages;

import network.marble.quickqueue.parties.Party;

import java.util.ArrayList;
import java.util.UUID;

public class GameJoinData {
    public UUID partyId, gameId;
    public UUID[] members;
    public boolean isPriority;

    public GameJoinData(Party p, UUID gameId, boolean isPriority){
        partyId = p.getPartyID();
        this.gameId = gameId;
        this.isPriority = isPriority;

        //Create member list that includes leader
        UUID[] list;
        ArrayList<UUID> all = new ArrayList();
        all.addAll(p.getMembers());
        all.add(p.getLeader());
        list = all.toArray(new UUID[all.size()]);
        members = list;
    }
}
