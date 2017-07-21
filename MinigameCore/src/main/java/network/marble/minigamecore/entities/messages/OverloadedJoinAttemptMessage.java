package network.marble.minigamecore.entities.messages;

import java.util.List;
import java.util.UUID;

public class OverloadedJoinAttemptMessage extends Message {
    public int count;

    public long stamp;
    public UUID rejectedPlayer;
    public boolean isPlayerMode;
    public List<String> actualPlayerList;

    public OverloadedJoinAttemptMessage() {
        super(221);
    }
}
