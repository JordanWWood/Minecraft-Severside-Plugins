package network.marble.dataaccesslayer.models.plugins.friends;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Friend extends BaseModel<Friend> {
    public Friend(){
        super("plugins/friends");
    }

    @Getter @Setter

    public UUID sender;

    @Getter @Setter
    public UUID receiver;

    @Getter @Setter
    public boolean accepted;

    @Getter @Setter
    public long accepted_at;

    public List<Friend> getFriendsOf(UUID uuid) throws APIException {
        return getsFromURL(urlEndPoint+"/of/"+uuid.toString());
    }

    public List<Friend> getFriendRequestsInvolving(UUID uuid) throws APIException {
        return getsFromURL(urlEndPoint+"/requests/"+uuid.toString());
    }

    public List<Friend> getFriendRequestsTo(UUID uuid) throws APIException {
        return getsFromURL(urlEndPoint+"/requests/to/"+uuid.toString());
    }

    public List<Friend> getFriendRequestsFrom(UUID uuid) throws APIException {
        return getsFromURL(urlEndPoint+"/requests/from/"+uuid.toString());
    }

    @Override
    public Class<?> getTypeClass() {
        return Friend.class;
    }
}
