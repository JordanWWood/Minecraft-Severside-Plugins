package network.marble.dataaccesslayer.models.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import network.marble.dataaccesslayer.common.Context;
import network.marble.dataaccesslayer.entities.AnalyticsTree;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAnalytic extends BaseModel<UserAnalytic> {
    public UserAnalytic() {
        super("users");
    }

    @Getter @Setter
    public UUID user_id;

    public AnalyticsTree analytics = new AnalyticsTree(0);

    @Override
    public UserAnalytic get(UUID id) throws APIException {
        return getFromURL(urlEndPoint+"/"+id+"/analytics");
    }

    @Override
    public boolean update() throws APIException {
        return _update(urlEndPoint + "/" + this.getId().toString()+"/analytics").success;
    }

    @Override
    public boolean insert() throws APIException {
        return _insert(urlEndPoint+"/analytics").success;
    }

    @Override
    public boolean delete() throws APIException {
        return _delete(urlEndPoint + "/" + this.getId().toString()+"/analytics").success;
    }

    public UserAnalytic getSpecific(UUID id, String identifier) throws APIException {
        return getFromURL(urlEndPoint+"/"+id+"/analytics/"+identifier);
    }

    public boolean alterAnalyticsValue(String identifier, long amount) throws APIException {
        Request request = context.getRequest(urlEndPoint+"/"+user_id+"/analytics/"+identifier+"/alter/"+amount);
        try (Response response = Context.getClient().newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteAnalyticsValue(String identifier) throws APIException {
        Request request = context.deleteRequest(urlEndPoint+"/"+user_id+"/analytics/"+identifier);
        try (Response response = Context.getClient().newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteAnalytics() throws APIException {
        Request request = context.deleteRequest(urlEndPoint+"/"+user_id+"/analytics/");
        try (Response response = Context.getClient().newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Class<?> getTypeClass() {
        return UserAnalytic.class;
    }
}
