package network.marble.dataaccesslayer.models.plugins.vanity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VanityItem extends BaseModel<VanityItem> {
    public VanityItem(){
        super("plugins/vanity");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public boolean consumable;

    @Getter @Setter
    public long price;

    @Getter @Setter
    public String category;

    @Getter @Setter
    public String slot;

    @Getter @Setter
    public Type type;

    @Getter @Setter
    public Map<String, ItemInformation> itemInformation;

    @Override
    public Class<?> getTypeClass() {
        return VanityItem.class;
    }

    public List<VanityItem> getByCategory(String category) throws APIException {
        return getsFromURL(urlEndPoint+"/categories/"+category);
    }

    public VanityItem getByName(String name) throws APIException {
        return getFromURL(urlEndPoint+"/name/"+name);
    }
}
