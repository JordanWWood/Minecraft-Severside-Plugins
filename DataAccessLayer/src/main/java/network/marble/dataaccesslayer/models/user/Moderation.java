package network.marble.dataaccesslayer.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Moderation {
    public boolean muted;

    public long ban_end_time;

    public long mute_end_time;

    public boolean banned;
}
