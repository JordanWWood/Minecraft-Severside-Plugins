package network.marble.minigamecore.entities.messages;

import network.marble.minigamecore.entities.game.GameStatus;

public class GameStatusUpdateMessage extends Message {
    public GameStatus status;

    public long stamp;

    public GameStatusUpdateMessage() {
        super(51);
    }
}
