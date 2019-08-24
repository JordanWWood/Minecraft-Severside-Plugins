package network.marble.minigamecore.entities.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MinigameEvent extends Event {
    private final static HandlerList handlers = new HandlerList();

    public MinigameEvent() {
        super(false);
    }

    public MinigameEvent(boolean isAsync) {
        super(isAsync);
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
