package network.marble.minigamecore.entities.analytics.game.timeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimelineEvent extends AnalyticEvent {
    private final String phrase;
    private final Map<String, String> tags;

    private final Long timeSinceStart;

    public TimelineEvent(String phrase, Long startTime, Map<String, String> tags) {
        super(AnalyticEventType.Timeline);
        this.phrase = phrase;
        this.timeSinceStart = System.currentTimeMillis() - startTime;
        this.tags = tags;
    }
}
