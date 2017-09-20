package network.marble.minigamecore.entities.events.player;

import lombok.Getter;
import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.player.MiniGamePlayer;

public class PlayerConnectEvent extends MinigameEvent {
    @Getter
    private MiniGamePlayer miniGamePlayer;

    public PlayerConnectEvent(MiniGamePlayer miniGamePlayer) {
        this.miniGamePlayer = miniGamePlayer;
    }
}
