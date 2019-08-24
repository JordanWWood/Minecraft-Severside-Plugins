package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;

import java.util.UUID;

@Data
public class WorldSetEvent extends ServerEvent {

    private UUID instanceId;

    private final String worldName;

    private final boolean permanentWorld;

    public WorldSetEvent(String worldName, boolean permanentWorld) {
        super("WorldSet");
        this.worldName = worldName;
        this.permanentWorld = permanentWorld;
    }

    public WorldSetEvent(String worldName) {
        super("WorldSet");
        this.worldName = worldName;
        this.permanentWorld = false;
    }
}
