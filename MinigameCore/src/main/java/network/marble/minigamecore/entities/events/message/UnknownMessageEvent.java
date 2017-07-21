package network.marble.minigamecore.entities.events.message;

import network.marble.minigamecore.entities.events.MinigameEvent;

import java.util.Map;

public class UnknownMessageEvent extends MinigameEvent {
    public String body;
    public Map<String, Object> headers;

    public UnknownMessageEvent(String body, Map<String, Object> headers) {
        super(true);
        this.body = body;
        this.headers = headers;
    }
}
