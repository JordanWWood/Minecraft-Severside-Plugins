package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class ServerAvailableMessage extends Message {
    public ServerAvailableMessage() {
        super(50);
    }

    public UUID serverId;
}
