package network.marble.dataaccesslayer.models;


import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

public class Game extends BaseModel<Game> {
    public Game(){
        super("games", "games", "game");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String filename;

    @Getter @Setter
    public String description;

    public Map<String, Object> iconMap;

    @Getter @Setter
    public Boolean isLive;

    @Getter @Setter
    public Boolean isActive;

    @Getter @Setter
    public Boolean isBeta;

    /*public Map<String, Object> getIconMap() {
    	Gson g = new Gson();
    	Map<String, Object> map = null;
    	if (iconMap != null && !iconMap.isEmpty()) {
    		map = g.fromJson(iconMap, new TypeToken<Map<String, Object>>(){}.getType());
    	}
		return map;
    }

    public void setIconMap(Map<String, Object> map) {
        Gson g = new Gson();
        iconMap = g.toJson(map);
    }*/

    public Game getByName(String name) throws APIException {
        return getSingle(urlEndPoint+"/name/"+name);
    }

    @Override
    public Class<?> getTypeClass() {
        return Game.class;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                ", filename='" + filename + '\'' +
                ", description='" + description + '\'' +
                ", iconMap=" + iconMap +
                ", isLive=" + isLive +
                ", isActive=" + isActive +
                ", isBeta=" + isBeta +
                "} " + super.toString();
    }
}
