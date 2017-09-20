package network.marble.dataaccesslayer.models.plugins.currency;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;
import org.bukkit.Material;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
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
}
