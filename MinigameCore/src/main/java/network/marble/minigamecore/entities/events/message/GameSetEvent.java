package network.marble.minigamecore.entities.events.message;

import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.messages.GameModeSetMessage;

public class GameSetEvent extends MinigameEvent {
    public GameModeSetMessage message;

    public GameSetEvent(GameModeSetMessage message) {
        super(false);
        this.message = message;
    }
}
