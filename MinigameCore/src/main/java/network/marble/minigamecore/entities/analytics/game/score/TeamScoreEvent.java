package network.marble.minigamecore.entities.analytics.game.score;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeamScoreEvent extends AnalyticEvent {
    private final long value;

    private final String scoreField;

    private final String teamName;

    public TeamScoreEvent(String teamName) {
        super(AnalyticEventType.Score);
        this.scoreField = "default";
        this.value = 1;
        this.teamName = teamName;

    }

    public TeamScoreEvent(String teamName, String scoreField) {
        super(AnalyticEventType.Score);
        this.scoreField = scoreField;
        this.value = 1;
        this.teamName = teamName;
    }

    public TeamScoreEvent(String teamName, long value) {
        super(AnalyticEventType.Score);
        this.scoreField = "default";
        this.value = value;
        this.teamName = teamName;
    }

    public TeamScoreEvent(String teamName, String scoreField, long value) {
        super(AnalyticEventType.Score);
        this.scoreField = scoreField;
        this.value = value;
        this.teamName = teamName;
    }
}
