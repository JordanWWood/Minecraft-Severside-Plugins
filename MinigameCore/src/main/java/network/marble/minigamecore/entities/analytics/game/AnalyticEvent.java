package network.marble.minigamecore.entities.analytics.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.server.ServerEvent;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AnalyticEvent extends ServerEvent {

    private UUID gameId;

    private UUID gameModeId;

    private final AnalyticEventType analyticEventType;

    public AnalyticEvent(AnalyticEventType type) {
        super("Game");
        this.analyticEventType = type;
    }
}
