package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class ServerUnavailableMessage extends Message {
    public ServerUnavailableMessage() {
        super(47);
    }

    public UUID serverId;
}
