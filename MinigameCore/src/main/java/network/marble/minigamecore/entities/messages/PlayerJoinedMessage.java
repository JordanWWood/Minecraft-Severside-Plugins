package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class PlayerJoinedMessage extends Message {
	public UUID playerId;
    public boolean isPlayerMode;//Whether they user is a normal player (true) or a spectator/staff

    public PlayerJoinedMessage(UUID playerId, boolean isPlayerMode) {
        super(54);
        this.playerId = playerId;
        this.isPlayerMode = isPlayerMode;
    }
}
