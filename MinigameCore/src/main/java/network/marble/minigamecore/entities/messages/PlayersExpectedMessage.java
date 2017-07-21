package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class PlayersExpectedMessage extends Message {
    public UUID[] uuids;

    public PlayersExpectedMessage() {
        super(52);
    }
}
