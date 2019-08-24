package network.marble.minigamecore.entities.analytics.game.team;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeamEvent extends AnalyticEvent {
    private final String teamName;

    private final String teamEventType;

    private final String details;

    public TeamEvent(String teamName, String teamEventType, String details) {
        super(AnalyticEventType.Team);
        this.teamName = teamName;
        this.teamEventType = teamEventType;
        this.details = details;
    }
}
