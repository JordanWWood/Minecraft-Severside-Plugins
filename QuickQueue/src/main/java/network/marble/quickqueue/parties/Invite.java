package network.marble.quickqueue.parties;

import java.util.UUID;

import lombok.Getter;

public class Invite {
    @Getter
    UUID sourcePartyID;
    @Getter
    UUID sourcePlayer;

    public Invite(UUID sourcePartyID, UUID sourcePlayer){
        this.sourcePartyID = sourcePartyID;
        this.sourcePlayer = sourcePlayer;
        //TODO expire automatically after 30 seconds with a run later
    }
}
