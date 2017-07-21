package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class RejectedPlayerExpectationMessage extends Message {
    public UUID uuid;
    public boolean isPlayerMode;

    public RejectedPlayerExpectationMessage() {
        super(30);
    }
}
