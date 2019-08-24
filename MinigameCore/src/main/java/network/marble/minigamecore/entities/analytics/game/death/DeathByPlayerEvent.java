package network.marble.minigamecore.entities.analytics.game.death;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeathByPlayerEvent extends DeathEvent {
    private final String killerName;
    private final UUID killerUUID;

    public DeathByPlayerEvent(Player victim, Player killer, String deathDetails ) {
        super(victim, "BYPLAYER", deathDetails);
        this.killerName = killer.getName();
        this.killerUUID = killer.getUniqueId();
    }
}
