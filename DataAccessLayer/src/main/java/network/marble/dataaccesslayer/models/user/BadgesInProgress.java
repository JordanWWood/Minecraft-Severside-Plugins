package network.marble.dataaccesslayer.models.user;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.badge.Badge;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BadgesInProgress {
    public UUID badge_id;

    public int progress;

    public List<String> flags;

    @JsonIgnore
    public Badge getBadge() throws APIException{
        return new Badge().get(badge_id);
    }
}
