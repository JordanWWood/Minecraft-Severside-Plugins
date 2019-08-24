package network.marble.quickqueue.messages;

import lombok.Getter;

import java.util.UUID;

public class PartyQueueData {
    @Getter
    UUID partyId, gameModeId;
    @Getter
    boolean isQueued;
}
