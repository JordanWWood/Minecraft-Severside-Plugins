package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.entities.Result;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.CacheManager;
import network.marble.dataaccesslayer.models.base.BaseModel;
import okhttp3.Request;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Case extends BaseModel<Case> {
    public Case(){
        super("plugins/moderation/cases", "cases", "case");
    }

    @Getter @Setter
    public long created_at;

    @Getter @Setter
    public long closed_at;

    @Getter @Setter
    public UUID judgementee_id;

    @Getter @Setter
    public UUID created_by;

    @Getter @Setter
    public UUID assignee_id;

    @Getter @Setter
    public boolean pardoned;

    @Getter @Setter
    public UUID judgement_session_id;

    @Getter @Setter
    public CaseOutcome outcome = CaseOutcome.Undecided;

    @Getter @Setter
    public List<UUID> punishments;

    @Getter @Setter
    public String description;

    @Getter @Setter
    public Long outcome_duration;

    @Getter @Setter
    public List<UUID> reports;

    @Override
    public Class<?> getTypeClass() {
        return Case.class;
    }

    public List<Case> getActiveByJudgementeeOutcome(UUID id, CaseOutcome outcome) throws APIException {
        return getMultiple(urlEndPoint+"/judgementee/"+id+"/"+outcome+"/active");
    }

    public List<Case> getByJudgementee(UUID id) throws APIException {
        return getMultiple(urlEndPoint+"/judgementee/"+id);
    }

    public List<Case> getOpenByJudgementee(UUID id) throws APIException {
        return getMultiple(urlEndPoint+"/judgementee/"+id+"/open");
    }

    public List<Case> getByClosedJudgementee(UUID id) throws APIException {
        return getMultiple(urlEndPoint+"/judgementee/"+id+"/closed");
    }

    public List<Case> getByAssignee(UUID id) throws APIException {
        return getMultiple(urlEndPoint+"/assignee/"+id);
    }

    public List<Case> getOpenByAssignee(UUID id) throws APIException {
        return getMultiple(urlEndPoint+"/assignee/"+id+"/open");
    }

    public List<Case> getByClosedAssignee(UUID id) throws APIException {
        return getMultiple(urlEndPoint+"/assignee/"+id+"/closed");
    }

    public Case saveAndReturn() throws APIException {
        return this.exists() ? updateAndReturn() : insertAndReturn();
    }

    public Case updateAndReturn() throws APIException {
        return updateAndReturn(urlEndPoint + "/" + this.id.toString());
    }

    protected Case updateAndReturn(String url) throws APIException {
        String json = serializeModel(this);
        Request r = context.putRequest(url, json);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        Result result = deserializeModel(returned, Result.class);
        if (result.getReplaced() > 0 || result.getUnchanged() > 0) {
            if (CacheManager.getInstance().getCache().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().getCache().replace(this.id, this);
            return this;
        } else return null;
    }

    public Case insertAndReturn() throws APIException {
        return insertAndReturn(urlEndPoint);
    }

    @SuppressWarnings("unchecked")
    protected Case insertAndReturn(String url) throws APIException {
        String json = serializeModel((Case)this);
        Request r = context.postRequest(url, json);
        String returned = context.executeRequest(r);
        utils.errorCheck(returned);
        Result result = deserializeModel(returned, Result.class);
        if ((result.getInserted() > 0) && result.getGeneratedKeys().size() > 0) {
            this.id = result.getGeneratedKeys().get(0);
            if (CacheManager.getInstance().getCache().containsKey(this.id) && CacheManager.enabled) CacheManager.getInstance().getCache().replace(this.id, this);
            return this;
        } else return null;
    }
}
