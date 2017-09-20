package network.marble.minigamecore.entities.messages;

import java.util.UUID;

import network.marble.minigamecore.entities.game.GameStatus;

public class ServerDataMessage extends Message{
    public UUID serverId, gameId;
    public UUID[] currentPlayers;
    public GameStatus status;

    public ServerDataMessage() {
        super(49);
    }
}
