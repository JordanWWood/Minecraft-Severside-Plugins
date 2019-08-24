package network.marble.dataaccesslayer.models.plugins.bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Spray extends BaseModel<Spray> {
    public Spray() {
        super("plugins/bitmap/sprays");
    }

    public String[] imageData;
    
    public String title;
    
    public String author;

    @Override
    public Class<?> getTypeClass() {
        return Spray.class;
    }
}