package network.marble.dataaccesslayer.models.plugins.vanity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemInformation {
    @Getter @Setter
    public String material;

    @Getter @Setter
    public UUID skinUUID;

    @Getter @Setter
    public String skinSecret;

    @Getter @Setter
    public String skinTexture;

    @Getter @Setter
    public String data;
}
