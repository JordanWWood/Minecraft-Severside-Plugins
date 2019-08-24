package network.marble.dataaccesslayer.models.plugins.adverts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;
import java.util.List;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Advert extends BaseModel<Advert> {
    public Advert(){
        super("plugins/adverts");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String type;

    @Getter @Setter
    public List<String> message;

    @Getter @Setter
    public int frequency;

    @Override
    public Class<?> getTypeClass() {
        return Advert.class;
    }

    @Override
    public String toString() {
        return "Advert{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", message=" + message +
                "} " + super.toString();
    }
}
