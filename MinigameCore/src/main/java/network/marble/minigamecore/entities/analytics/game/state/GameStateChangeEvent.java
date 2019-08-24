package network.marble.minigamecore.entities.analytics.game.state;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameStateChangeEvent extends AnalyticEvent {
    private String from;
    private String to;

    public GameStateChangeEvent() { super(AnalyticEventType.GameStateChange); }
}
