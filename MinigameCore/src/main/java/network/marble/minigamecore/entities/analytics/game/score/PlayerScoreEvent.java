package network.marble.minigamecore.entities.analytics.game.score;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerScoreEvent extends AnalyticEvent {
    private final long value;

    private final String scoreField;

    private final String playerName;

    private final UUID playerUUID;

    public PlayerScoreEvent(Player player) {
        super(AnalyticEventType.Score);
        this.scoreField = "default";
        this.value = 1;
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();

    }

    public PlayerScoreEvent(Player player, String scoreField) {
        super(AnalyticEventType.Score);
        this.scoreField = scoreField;
        this.value = 1;
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }

    public PlayerScoreEvent(Player player, long value) {
        super(AnalyticEventType.Score);
        this.scoreField = "default";
        this.value = value;
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }

    public PlayerScoreEvent(Player player, String scoreField, long value) {
        super(AnalyticEventType.Score);
        this.scoreField = scoreField;
        this.value = value;
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }
}
