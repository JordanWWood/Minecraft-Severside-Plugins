package network.marble.dataaccesslayer.models.user;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.dataaccesslayer.models.plugins.badge.Badge;

public class UserBadge {
    public UUID badge_id;

    public int progress;

    public long timeEarned;

    @JsonIgnore
    public Badge getBadge() throws APIException{
    	return new Badge().get(badge_id);
    }

    @Override
    public String toString() {
        return "UserBadge{" +
                "badge_id=" + badge_id +
                ", progress=" + progress +
                ", timeEarned=" + timeEarned +
                '}';
    }
}
