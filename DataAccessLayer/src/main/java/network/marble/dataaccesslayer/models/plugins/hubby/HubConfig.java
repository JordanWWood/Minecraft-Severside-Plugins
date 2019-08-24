package network.marble.dataaccesslayer.models.plugins.hubby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.entities.Vector;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HubConfig extends BaseModel<HubConfig> {
    public HubConfig() {
        super("plugins/hubby/configs");
    }
    
    public String configName;

    public boolean rainByDefault;

    public int defaultTime = 18000;
    
    public Vector spawn = new Vector(0, 100, 0);
    
    public String spawnWorld = "world";

    public Vector spawnDirection = new Vector(0, 0, 0);

    public String mapFile;

    public boolean scoreboardEnable;

    public boolean titleEnable;

    @Override
    public Class<?> getTypeClass() {
        return HubConfig.class;
    }

    public HubConfig getByName(String configName) throws APIException {
        return getFromURL(urlEndPoint+"/name/"+configName);
    }
}