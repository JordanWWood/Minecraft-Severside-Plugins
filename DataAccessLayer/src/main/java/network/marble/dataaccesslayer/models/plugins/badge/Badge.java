package network.marble.dataaccesslayer.models.plugins.badge;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Badge extends BaseModel<Badge> {
    public Badge(){
        super("plugins/badges");
    }
    
    public String name;
    public int maxProgress;
    public String description;
    public boolean isLimited;
    public HashMap<String, String> awards;
    public List<String> progressFlags;

    @Override
    public Class<?> getTypeClass() {
        return Badge.class;
    }
    
    public Badge getByPluginAndName(String plugin, String name) throws APIException {
        return getFromURL(String.format("%s/plugin/%s/%s", urlEndPoint, plugin, name));
    }
    
    public List<Badge> getByPlugin(String plugin) throws APIException {
        return getsFromURL(String.format("%s/plugin/%s", urlEndPoint, plugin));
    }
}
