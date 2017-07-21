package network.marble.minigamecore.entities.events.message;

import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.messages.ExpectPlayersMessage;

public class ExpectPlayersEvent extends MinigameEvent {
    public ExpectPlayersMessage message;

    public ExpectPlayersEvent(ExpectPlayersMessage message) {
        super(true);
        this.message = message;
    }
}
