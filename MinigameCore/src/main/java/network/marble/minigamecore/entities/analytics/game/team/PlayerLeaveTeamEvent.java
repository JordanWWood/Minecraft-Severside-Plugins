package network.marble.minigamecore.entities.analytics.game.team;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerLeaveTeamEvent extends TeamEvent {

    private final String playerName;

    private final UUID playerUUID;

    public PlayerLeaveTeamEvent(String teamName, Player player) {
        super(teamName, "LEAVE", "default");
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }

    public PlayerLeaveTeamEvent(String teamName, Player player, String details) {
        super(teamName, "LEAVE", details);
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }
}
