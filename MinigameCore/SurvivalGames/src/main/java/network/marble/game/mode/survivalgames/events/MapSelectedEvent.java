package network.marble.game.mode.survivalgames.events;

import lombok.Getter;
import network.marble.minigamecore.entities.events.MinigameEvent;

public class MapSelectedEvent extends MinigameEvent {
    @Getter private String mapName;

    public MapSelectedEvent(String mapName) {
        this.mapName = mapName;
    }
}
