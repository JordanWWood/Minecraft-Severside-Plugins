package network.marble.dataaccesslayer.models.plugins.moderation;


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
public class Case extends BaseModel<Case> {
    public Case(){
        super("plugins/moderation/cases");
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

    @Getter @Setter
    public boolean requires_review;

    @Getter @Setter
    public String pipeline;

    @Getter @Setter
    public String reason;

    @Override
    public Class<?> getTypeClass() {
        return Case.class;
    }

    public List<Case> getActiveByJudgementeeOutcome(UUID id, CaseOutcome outcome) throws APIException {
        return getsFromURL(urlEndPoint+"/judgementee/"+id+"/"+outcome.toValue()+"/active");
    }

    public List<Case> getByJudgementee(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/judgementee/"+id);
    }

    public List<Case> getOpenByJudgementee(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/judgementee/"+id+"/open");
    }

    public List<Case> getByClosedJudgementee(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/judgementee/"+id+"/closed");
    }

    public List<Case> getByAssignee(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/assignee/"+id);
    }

    public List<Case> getOpenByAssignee(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/assignee/"+id+"/open");
    }

    public List<Case> getByClosedAssignee(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/assignee/"+id+"/closed");
    }
}
