package network.marble.quickqueue;

import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import network.marble.dataaccesslayer.models.Game;
import network.marble.dataaccesslayer.models.GameMode;

public class GameSet {
    @Getter Game game;
    @Getter CopyOnWriteArrayList<GameMode> modes = new CopyOnWriteArrayList<>();

    public GameSet(Game game){
        this.game = game;
    }
}
