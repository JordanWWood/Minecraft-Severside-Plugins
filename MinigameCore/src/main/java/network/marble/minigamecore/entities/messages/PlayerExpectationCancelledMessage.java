package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class PlayerExpectationCancelledMessage extends Message {
    public UUID playerId;
    public boolean isPlayerMode;

    public PlayerExpectationCancelledMessage(UUID playerId, boolean isPlayerMode) {
        super(53);
        this.playerId = playerId;
        this.isPlayerMode = isPlayerMode;
    }
}
