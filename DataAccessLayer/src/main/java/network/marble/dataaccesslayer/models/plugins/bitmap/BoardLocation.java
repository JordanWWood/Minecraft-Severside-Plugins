package network.marble.dataaccesslayer.models.plugins.bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.entities.Vector;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardLocation extends BaseModel {
    @Getter @Setter
    private String serverType;
    @Getter @Setter
    private Vector bottomLeft;
    @Getter @Setter
    private String world;
    @Getter @Setter
    private Orientation orientation;
    @Setter
    private UUID board;
    @Getter @Setter
    private List<String> categories;

    public BoardLocation(){
        super("plugins/bitmap/boardlocations");
    }

    public BoardLocation(String urlEndPoint) {
        super(urlEndPoint);
    }

    public List<BoardLocation> getByServerType(String serverType) throws APIException{
        return getsFromURL(String.format("%s/servertype/%s", urlEndPoint, serverType));
    }

    public Board getBoard() throws APIException {
        return (Board) new Board().get(board);
    }

    public UUID getBoardId() throws APIException {
        return board;
    }

    @Override
    public Class<?> getTypeClass() {
        return BoardLocation.class;
    }

    public enum Orientation {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}