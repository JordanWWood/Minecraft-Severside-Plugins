package network.marble.minigamecore.entities.messages;

import network.marble.minigamecore.entities.player.PlayerType;

import java.util.UUID;

public class ExpectPlayersMessage extends Message {
	public UUID[] players;
    public UUID leaderId;
    public PlayerType type;

    public ExpectPlayersMessage() {
        super(61);
    }
}
