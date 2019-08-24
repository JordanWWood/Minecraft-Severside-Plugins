package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivePunishment extends BaseModel<ActivePunishment> {

    public ActivePunishment(){
        super("plugins/moderation/activepunishments");
    }

    @Getter @Setter
    private UUID uuid;

    @Getter @Setter
    private String ip;

    @Getter @Setter
    private long expiryDate;

    @Getter @Setter
    private ActivePunishmentActionType action;

    @Getter @Setter
    @JsonProperty("case_id")
    private UUID caseId;

    @Override
    public Class<?> getTypeClass() {
        return ActivePunishment.class;
    }

    @Deprecated
    public List<ActivePunishment> getByUuid(UUID id) throws APIException {
        return getsFromURL(urlEndPoint+"/uuid/"+id);
    }

    public Future<List<ActivePunishment>> getByUuidAsync(UUID id) {
        return executor.submit(() -> this.getByUuid(id));
    }

    @Deprecated
    public List<ActivePunishment> getByUuidAndAction(UUID id, ActivePunishmentActionType action) throws APIException {
        return getsFromURL(urlEndPoint+"/uuid/"+id+"/action/"+action);
    }

    public Future<List<ActivePunishment>> getByUuidAndActionAsync(UUID id, ActivePunishmentActionType action) {
        return executor.submit(() -> this.getByUuidAndAction(id, action));
    }

    @Deprecated
    public List<ActivePunishment> getByIp(String ip) throws APIException {
        return getsFromURL(urlEndPoint+"/ip/"+ip);
    }

    public Future<List<ActivePunishment>> getByIpAsync(String ip) {
        return executor.submit(() -> this.getByIp(ip));
    }

    @Deprecated
    public List<ActivePunishment> getByIpAndAction(String ip, ActivePunishmentActionType action) throws APIException {
        return getsFromURL(urlEndPoint+"/ip/"+ip+"/action/"+action);
    }

    public Future<List<ActivePunishment>> getByIpAndActionAsync(String ip, ActivePunishmentActionType action)  {
        return executor.submit(() -> this.getByIpAndAction(ip, action));
    }
}
