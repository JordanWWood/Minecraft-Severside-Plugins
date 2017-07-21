package network.marble.minigamecore.entities.events.game;

import network.marble.minigamecore.entities.events.MinigameEvent;
import network.marble.minigamecore.entities.game.GameStatus;

/**
 * Created by Duncan on 27/08/2016.
 */
public class GameStatusChangeEvent extends MinigameEvent {

    public GameStatus oldStatus;
    public GameStatus newStatus;

    public GameStatusChangeEvent(GameStatus oldStatus, GameStatus newStatus){
        super(false);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
