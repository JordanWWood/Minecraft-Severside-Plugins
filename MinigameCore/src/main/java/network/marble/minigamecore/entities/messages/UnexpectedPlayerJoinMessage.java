package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class UnexpectedPlayerJoinMessage extends Message {
    public UUID playerId;
    

    public UnexpectedPlayerJoinMessage() {
        super(222);
    }
}
