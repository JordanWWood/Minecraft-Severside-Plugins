package network.marble.minigamecore.entities.events.message;

import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.messages.PingMessage;

public class PingEvent extends MinigameEvent {
    public PingMessage message;
    public PingEvent(PingMessage message) {
        super(true);
        this.message = message;
    }
}
