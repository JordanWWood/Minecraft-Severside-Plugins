package network.marble.dataaccesslayer.models.plugins.bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;
import java.util.List;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
