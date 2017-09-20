package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rank extends BaseModel<Rank> {
    public Rank(){
        super("plugins/moderation/ranks", "ranks", "rank");
    }

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String prefix;

    @Getter @Setter
    public String suffix;

    @Getter @Setter
    public UUID parent_id;

    @Getter @Setter
    public boolean isPriority;

    @Getter @Setter
    public List<String> permissions;

    public Rank getFull(UUID id) throws APIException {
        return getSingle(urlEndPoint+"/"+id.toString()+"/full");
    }

    @Override
    public Class<?> getTypeClass() {
        return Rank.class;
    }
}
