package network.marble.dataaccesslayer.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ForumData {
    public int forumId;

    public boolean registered;
    
    public String forumKey;

    @JsonIgnore
    public long forumKeyExpires_at;

    @Override
    public String toString() {
        return "ForumData{" +
                "forumId=" + forumId +
                ", registered=" + registered +
                ", forumKey='" + forumKey + '\'' +
                ", forumKeyExpires_at=" + forumKeyExpires_at +
                '}';
    }
}
