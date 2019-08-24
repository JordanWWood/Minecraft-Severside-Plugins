package network.marble.dataaccesslayer.models.plugins.bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Board extends BaseModel {
    @Getter @Setter
    private String mapImageUrl;
    @Getter @Setter
    private Integer width, height;

    public Board(){
        super("plugins/bitmap/boards");
    }

    public Board(String urlEndPoint) {
        super(urlEndPoint);
    }

    @Override
    public Class<?> getTypeClass() {
        return Board.class;
    }
}