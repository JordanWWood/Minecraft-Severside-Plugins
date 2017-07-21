package network.marble.dataaccesslayer.models.plugins.currency;


import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;
import org.bukkit.Material;

public class Currency extends BaseModel<Currency> {
    public Currency(){
        super("plugins/currency/currencies", "currencies", "currency");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String material;

    public Material getBukkitMaterial() {
        return Material.valueOf(this.material);
    }

    public void setBukkitMaterial(Material material) {
        this.material = material.toString();
    }

    public Currency getByName(String name) throws APIException {
        return getSingle(urlEndPoint+"/name/"+name);
    }

    @Override
    public Class<?> getTypeClass() {
        return Currency.class;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "name='" + name + '\'' +
                ", material='" + material + '\'' +
                "} " + super.toString();
    }
}
