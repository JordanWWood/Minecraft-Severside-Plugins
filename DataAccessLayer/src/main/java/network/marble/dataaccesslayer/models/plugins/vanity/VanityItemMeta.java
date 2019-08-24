package network.marble.dataaccesslayer.models.plugins.vanity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VanityItemMeta {
    @Getter @Setter
    public String slot;

    @Getter @Setter
    public String skinUUID;

    @Getter @Setter
    public String skinTexture;

    @Getter @Setter
    public String data;
}
