package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;

import java.util.UUID;

@Data
public class MiniGameUnsetEvent extends ServerEvent {

    private UUID instanceId;

    public MiniGameUnsetEvent() {
        super("MiniGameUnset");
    }
}
