package network.marble.minigamecore.entities.events.message;

import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.messages.CancelExpectedPlayerMessage;

public class CancelExpectedPlayerEvent extends MinigameEvent {
    public CancelExpectedPlayerMessage message;

    public CancelExpectedPlayerEvent(CancelExpectedPlayerMessage message) {
        super(true);
        this.message = message;
    }
}
