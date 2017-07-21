package network.marble.dataaccesslayer.models.plugins.hubby;

import network.marble.dataaccesslayer.entities.Vector;
import network.marble.dataaccesslayer.models.base.BaseModel;

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

    @Override
    public String toString() {
        return "HubConfig{" +
                "configName='" + configName + '\'' +
                ", rainByDefault=" + rainByDefault +
                ", defaultTime=" + defaultTime +
                ", spawn=" + spawn +
                ", spawnWorld='" + spawnWorld + '\'' +
                ", spawnDirection=" + spawnDirection +
                ", mapFile='" + mapFile + '\'' +
                "} " + super.toString();
    }
}