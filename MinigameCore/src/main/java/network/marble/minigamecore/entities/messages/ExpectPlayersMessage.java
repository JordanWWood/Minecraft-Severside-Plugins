package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class ExpectPlayersMessage extends Message {
    public UUID[] playerIds;
    public UUID partyId;
    public int type = 1;

    public ExpectPlayersMessage() {
        super(61);
    }
}
