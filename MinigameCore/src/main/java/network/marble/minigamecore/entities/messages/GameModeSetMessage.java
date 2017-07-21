package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class GameModeSetMessage extends Message{
    public UUID uuid;

    public GameModeSetMessage() {
        super(60);
    }
}
