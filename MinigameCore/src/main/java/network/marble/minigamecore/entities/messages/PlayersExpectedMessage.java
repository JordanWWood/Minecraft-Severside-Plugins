package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class PlayersExpectedMessage extends Message {
    public UUID[] playerID;
    public String serverName;

    public PlayersExpectedMessage() {
        super(52);
    }
}
