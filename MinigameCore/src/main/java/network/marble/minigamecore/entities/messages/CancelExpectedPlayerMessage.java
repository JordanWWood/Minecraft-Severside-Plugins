package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class CancelExpectedPlayerMessage extends Message {
    public UUID uuid;

    public CancelExpectedPlayerMessage() {
        super(62);
    }
}
