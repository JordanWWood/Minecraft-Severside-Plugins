package network.marble.minigamecore.entities.analytics.game.death;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeathByMobEvent extends DeathEvent {
    private final String mobType;

    public DeathByMobEvent(Player victim, String mobType, String deathDetails ) {
        super(victim, "BYMOB", deathDetails);
        this.mobType = mobType;
    }
}
