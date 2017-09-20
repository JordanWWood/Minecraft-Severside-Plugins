package network.marble.minigamecore.entities.events.game;

import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.game.GameStatus;

public class GameStatusChangeEvent extends MinigameEvent {

    public GameStatus oldStatus;
    public GameStatus newStatus;

    public GameStatusChangeEvent(GameStatus oldStatus, GameStatus newStatus){
        super(false);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
