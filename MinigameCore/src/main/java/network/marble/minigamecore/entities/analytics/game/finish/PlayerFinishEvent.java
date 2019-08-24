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
public class PlayerFinishEvent extends AnalyticEvent {


    private final Map<UUID, String> winners;

    private final Map<UUID, String> losers;

    private final String finishEventType;

    public PlayerFinishEvent(List<Player> winners, List<Player> losers) {
        super(AnalyticEventType.Finish);

        this.winners = winners.stream().collect(Collectors.toMap(Player::getUniqueId, Player::getName));

        this.losers = losers.stream().collect(Collectors.toMap(Player::getUniqueId, Player::getName));

        this.finishEventType = "PLAYERS";
    }

    public PlayerFinishEvent(Map<UUID, String> winners, Map<UUID, String> losers) {
        super(AnalyticEventType.Finish);

        this.winners = winners;
        this.losers = losers;

        this.finishEventType = "PLAYERS";
    }
}
