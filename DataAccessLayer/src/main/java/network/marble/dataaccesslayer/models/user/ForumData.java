package network.marble.dataaccesslayer.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumData {
    public int forumId;

    public boolean registered;
    
    public String forumKey;

    @JsonIgnore
    public long forumKeyExpires_at;
}
