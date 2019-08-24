package network.marble.minigamecore.entities.analytics.game.finish;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeamFinishEvent extends AnalyticEvent {


    private final List<String> winners;

    private final List<String> losers;

    private final String finishEventType;

    public TeamFinishEvent(List<String> winners, List<String> losers) {
        super(AnalyticEventType.Finish);

        this.winners = winners;

        this.losers = losers;

        this.finishEventType = "TEAMS";
    }
}
