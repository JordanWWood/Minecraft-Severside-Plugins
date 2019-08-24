package network.marble.minigamecore.entities.analytics.game.death;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeathByEnvironmentEvent extends DeathEvent {
    private final String environmentFactor;

    public DeathByEnvironmentEvent(Player victim, String environmentFactor, String deathDetails ) {
        super(victim, "BYENVIRONMENT", deathDetails);
        this.environmentFactor = environmentFactor;
    }
}
