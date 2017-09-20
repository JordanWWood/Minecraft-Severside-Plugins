package network.marble.dataaccesslayer.models.plugins.badge;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Badge extends BaseModel<Badge> {
    public Badge(){
        super("plugins/badges", "badges", "badge");
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
        return getSingle(String.format("%s/plugin/%s/%s", urlEndPoint, plugin, name));
    }
    
    public List<Badge> getByPlugin(String plugin) throws APIException {
        return getMultiple(String.format("%s/plugin/%s", urlEndPoint, plugin));
    }
}
