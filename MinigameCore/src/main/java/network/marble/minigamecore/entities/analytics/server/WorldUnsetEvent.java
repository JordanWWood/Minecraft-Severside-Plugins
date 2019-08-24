package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;

import java.util.UUID;

@Data
public class WorldUnsetEvent extends ServerEvent {

    private UUID instanceId;

    private final String worldName;

    public WorldUnsetEvent(String worldName) {
        super("WorldUnset");
        this.worldName = worldName;
    }
}
