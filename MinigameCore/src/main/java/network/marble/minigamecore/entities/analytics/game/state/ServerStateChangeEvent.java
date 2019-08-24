package network.marble.minigamecore.entities.analytics.game.state;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;
import network.marble.minigamecore.entities.game.GameStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerStateChangeEvent extends AnalyticEvent {
    private final GameStatus from;
    private final GameStatus to;

    public ServerStateChangeEvent(GameStatus from, GameStatus to) { super(AnalyticEventType.ServerStateChange);
        this.from = from;
        this.to = to;
    }
}
