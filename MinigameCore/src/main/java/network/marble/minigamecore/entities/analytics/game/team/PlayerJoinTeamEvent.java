package network.marble.minigamecore.entities.analytics.game.team;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerJoinTeamEvent extends TeamEvent {

    private final String playerName;

    private final UUID playerUUID;

    public PlayerJoinTeamEvent(String teamName, Player player) {
        super(teamName, "JOIN", "default");
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }

    public PlayerJoinTeamEvent(String teamName, Player player, String details) {
        super(teamName, "JOIN", details);
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }
}
