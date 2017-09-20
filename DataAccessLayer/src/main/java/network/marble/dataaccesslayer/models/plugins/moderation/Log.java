package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Log {

    @Getter @Setter
    private long timestamp;

    @Getter @Setter
    private UUID user_id;

    @Getter @Setter
    private String message;
}
