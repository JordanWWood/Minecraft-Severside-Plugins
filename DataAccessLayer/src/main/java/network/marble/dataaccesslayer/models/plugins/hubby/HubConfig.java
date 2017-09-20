package network.marble.dataaccesslayer.models.plugins.hubby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.entities.Vector;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HubConfig extends BaseModel<HubConfig> {
    public HubConfig() {
        super("plugins/hubby/configs", "configs", "config");
    }
    
    public String configName;

    public boolean rainByDefault;

    public int defaultTime;
    
    public Vector spawn = new Vector(0, 100, 0);
    
	public String spawnWorld = "world";
	
	public Vector spawnDirection = new Vector(0, 0, 0);

	public String mapFile;

    @Override
    public Class<?> getTypeClass() {
        return HubConfig.class;
    }
}