package network.marble.dataaccesslayer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameAnalytic extends BaseModel<GameAnalytic> {

    public GameAnalytic() {
        super("games/analytics");
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
}
