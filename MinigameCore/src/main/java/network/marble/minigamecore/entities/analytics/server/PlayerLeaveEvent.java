package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class PlayerLeaveEvent extends ServerEvent {

    private UUID instanceId;

    private final String playerName;

    private final UUID playerUUID;

    public PlayerLeaveEvent(String playerName, UUID playerUUID) {
        super("PlayerLeave");
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public PlayerLeaveEvent(Player player) {
        super("PlayerLeave");
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }
}
