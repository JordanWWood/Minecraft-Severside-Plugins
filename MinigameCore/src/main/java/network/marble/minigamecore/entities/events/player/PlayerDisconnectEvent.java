package network.marble.minigamecore.entities.events.player;

import lombok.Getter;
import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import org.bukkit.entity.Player;

public class PlayerDisconnectEvent extends MinigameEvent {
    @Getter
    private MiniGamePlayer miniGamePlayer;

    @Getter
    private Player player;

    @Getter
    private boolean kick;

    public PlayerDisconnectEvent(MiniGamePlayer miniGamePlayer, Player player, boolean kicked) {
        this.miniGamePlayer = miniGamePlayer;
        this.player = player;
        this.kick = kicked;
    }
}
