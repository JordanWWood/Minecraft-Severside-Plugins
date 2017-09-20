package network.marble.dataaccesslayer.models.plugins.bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image extends BaseModel<Image> {
    public Image() {
        super("plugins/bitmap/images", "images", "image");
    }

    public String imageData;

    public int mapItem_id;

    @Override
    public Class<?> getTypeClass() {
        return Image.class;
    }
}