package network.marble.dataaccesslayer.models;

import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.UUID;

public class GameAnalytic extends BaseModel<GameAnalytic> {

    public GameAnalytic() {
        super("games/analytics", "gameanalytics", "gameanalytic");
    }

    @Getter @Setter
    public UUID game_id;

    @Getter @Setter
    public long timestamp;

    @Getter @Setter
    public Object data;

    @Override
    public Class<?> getTypeClass() {
        return GameAnalytic.class;
    }

    @Override
    public String toString() {
        return "GameAnalytic{" +
                "game_id=" + game_id +
                ", timestamp=" + timestamp +
                ", data=" + data +
                "} " + super.toString();
    }
}
