package network.marble.dataaccesslayer.models.plugins.bitmap;

import network.marble.dataaccesslayer.models.base.BaseModel;

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

    @Override
    public String toString() {
        return "Image{" +
                "imageData='" + imageData + '\'' +
                ", mapItem_id=" + mapItem_id +
                "} " + super.toString();
    }
}