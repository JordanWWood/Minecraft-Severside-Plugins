package network.marble.minigamecore.entities.analytics.server;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class PlayerJoinEvent extends ServerEvent {

    private UUID instanceId;

    private final String playerName;

    private final UUID playerUUID;

    public PlayerJoinEvent(String playerName, UUID playerUUID) {
        super("PlayerJoin");
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public PlayerJoinEvent(Player player) {
        super("PlayerJoin");
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
    }
}
