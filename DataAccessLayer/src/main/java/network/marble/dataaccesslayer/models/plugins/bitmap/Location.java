package network.marble.dataaccesslayer.models.plugins.bitmap;

import network.marble.dataaccesslayer.models.base.BaseModel;
import java.util.List;

public class Location extends BaseModel<Location> {
    public Location(){
        super("plugins/bitmap/locations", "locations", "location");
    }

    public String name;

    public List<Image> images;

    public String image_ids;

    public String worldName;

    public String vectorTopLeft;

    public String vectorBottomRight;

    @Override
    public Class<?> getTypeClass() {
        return Location.class;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", images=" + images +
                ", image_ids='" + image_ids + '\'' +
                ", worldName='" + worldName + '\'' +
                ", vectorTopLeft='" + vectorTopLeft + '\'' +
                ", vectorBottomRight='" + vectorBottomRight + '\'' +
                "} " + super.toString();
    }
}
