package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class PlayerLeaveMessage extends Message{
    public UUID playerId;
    public boolean isPlayerMode;//Whether they user is a normal player (true) or a spectator/staff

    public PlayerLeaveMessage(UUID playerId, boolean isPlayerMode) {
        super(56);
        this.playerId = playerId;
        this.isPlayerMode = isPlayerMode;
    }
}
