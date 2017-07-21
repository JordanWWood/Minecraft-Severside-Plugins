package network.marble.dataaccesslayer.models.plugins.badge;

import java.util.List;
import java.util.UUID;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

public class Badge extends BaseModel<Badge> {
    public Badge(){
        super("plugins/badges", "badges", "badge");
    }
    
    public String name;
    public int progressLimit;
    public String description;
    public boolean givesBackgroundArt;
    public String parentPlugin;
    public boolean isLimited;

    @Override
    public Class<?> getTypeClass() {
        return Badge.class;
    }
    
    public Badge getByPluginAndName(String plugin, String name) throws APIException {
        return getSingle(String.format("%s/plugin/%s/%s", urlEndPoint, plugin, name));
    }
    
    public List<Badge> getByPlugin(String plugin) throws APIException {
        return getMultiple(String.format("%s/plugin/%s", urlEndPoint, plugin));
    }

    @Override
    public String toString() {
        return "Badge{" +
                "name='" + name + '\'' +
                ", progressLimit=" + progressLimit +
                ", description='" + description + '\'' +
                ", givesBackgroundArt=" + givesBackgroundArt +
                ", parentPlugin='" + parentPlugin + '\'' +
                ", isLimited=" + isLimited +
                "} " + super.toString();
    }
}
