package network.marble.dataaccesslayer.models.plugins.vanity;


import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.util.List;
import java.util.Map;

public class VanityItem extends BaseModel<VanityItem> {
    public VanityItem(){
        super("plugins/vanity", "vanities", "vanity");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public boolean consumable;

    @Getter @Setter
    public long price;

    @Getter @Setter
    public String category;

    @Override
    public Class<?> getTypeClass() {
        return VanityItem.class;
    }

    public List<VanityItem> getByCategory(String category) throws APIException {
        return getMultiple(urlEndPoint+"/categories/"+category);
    }

    public VanityItem getByName(String name) throws APIException {
        return getSingle(urlEndPoint+"/name/"+name);
    }

    @Override
    public String toString() {
        return "VanityItem{" +
                "name='" + name + '\'' +
                ", consumable=" + consumable +
                ", price=" + price +
                ", category='" + category + '\'' +
                "} " + super.toString();
    }
}
