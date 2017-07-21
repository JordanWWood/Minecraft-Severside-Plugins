package network.marble.dataaccesslayer.models.plugins.adverts;

import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.models.base.BaseModel;
import java.util.List;

public class Advert extends BaseModel<Advert> {
    public Advert(){
        super("plugins/adverts", "adverts", "advert");
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
