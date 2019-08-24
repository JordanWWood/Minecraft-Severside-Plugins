package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;
import network.marble.dataaccesslayer.entities.analytics.AdditiveEvent;

import java.util.UUID;

@Data
public abstract class ServerEvent extends AdditiveEvent {

    private UUID instanceId;

    private final String serverEventType;

    public ServerEvent(String serverEventType){
        this.serverEventType = serverEventType;
    }

}
