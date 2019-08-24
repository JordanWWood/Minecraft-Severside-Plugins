package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;

import java.util.UUID;

@Data
public class MiniGameSetEvent extends ServerEvent {

    private UUID instanceId;

    private final UUID gameId;

    private final UUID gameModeId;

    public MiniGameSetEvent(UUID gameId, UUID gameModeId) {
        super("MiniGameSet");
        this.gameId = gameId;
        this.gameModeId = gameModeId;
    }
}
