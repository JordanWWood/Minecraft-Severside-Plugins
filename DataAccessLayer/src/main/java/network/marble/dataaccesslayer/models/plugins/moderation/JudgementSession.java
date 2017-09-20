package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.entities.Result;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.CacheManager;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JudgementSession extends BaseModel<JudgementSession> {
    public JudgementSession() {
        super("plugins/moderation/judgementsessions", "judgementsessions", "judgementsession");
    }

    @Getter @Setter
    private long created_at;

    @Getter @Setter
    private long ended_on;

    @Getter @Setter
    private UUID judgementee;

    @Getter @Setter
    private HashMap<UUID, String> judgementers;

    @Getter @Setter
    private List<Log> logs;

    public JudgementSession saveAndReturn() throws APIException {
        return this.exists() ? updateAndReturn() : insertAndReturn();
    }

    public JudgementSession updateAndReturn() throws APIException {
        return updateAndReturn(urlEndPoint + "/" + this.id.toString());
    }

    protected JudgementSession updateAndReturn(String url) throws APIException {
        String json = serializeModel(this);
        Request r = context.putRequest(url, json);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        Result result = deserializeModel(returned, Result.class);
        if (result.getReplaced() > 0 || result.getUnchanged() > 0) {
            if (CacheManager.getInstance().getCache().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().getCache().remove(this.id);
            return this;
        } else return null;
    }

    public JudgementSession insertAndReturn() throws APIException {
        return insertAndReturn(urlEndPoint);
    }

    @SuppressWarnings("unchecked")
    protected JudgementSession insertAndReturn(String url) throws APIException {
        String json = serializeModel(this);
        Request r = context.postRequest(url, json);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        Result result = deserializeModel(returned, Result.class);
        if ((result.getInserted() > 0) && result.getGeneratedKeys().size() > 0) {
            this.id = result.getGeneratedKeys().get(0);
            if (CacheManager.getInstance().getCache().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().getCache().remove(this.id);
            return this;
        } else return null;
    }

    @Override
    public Class<?> getTypeClass() {
        return JudgementSession.class;
    }
}
