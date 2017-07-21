package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    @Setter
    public String permissions;

    public Rank getFull(UUID id) throws APIException {
        return getSingle(urlEndPoint+"/"+id.toString()+"/full");
    }

	public List<String> getPermissions() {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, String.class);
        try {
            return mapper.readValue(this.permissions, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<?> getTypeClass() {
        return Rank.class;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", parent_id=" + parent_id +
                ", isPriority=" + isPriority +
                ", permissions='" + permissions + '\'' +
                "} " + super.toString();
    }
}
