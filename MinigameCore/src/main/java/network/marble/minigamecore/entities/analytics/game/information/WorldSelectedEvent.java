package network.marble.minigamecore.entities.analytics.game.information;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorldSelectedEvent extends AnalyticEvent {
    private String worldName;
    private String author;

    public WorldSelectedEvent(String worldName, String author) {
        super(AnalyticEventType.GameInformation);
        this.worldName = worldName;
        this.author = author;
    }
}