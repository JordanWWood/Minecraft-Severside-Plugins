package network.marble.quickqueue.messages;

import network.marble.quickqueue.parties.Party;

import java.util.UUID;

public class GameLeaveData {
    public UUID partyId;

    public GameLeaveData(Party p){
        partyId = p.getPartyID();
    }
}
