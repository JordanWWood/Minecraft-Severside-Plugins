package network.marble.minigamecore.entities.analytics.game.death;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.game.AnalyticEventType;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeathEvent extends AnalyticEvent {

    private final String playerName;

    private final UUID playerUUID;

    private final String deathEventType;

    private final String deathDetails;

    public DeathEvent(Player player, String deathEventType, String deathDetails) {
        super(AnalyticEventType.Death);
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        this.deathEventType = deathEventType;
        this.deathDetails = deathDetails;
    }
}
