package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JudgementSession extends BaseModel<JudgementSession> {
    public JudgementSession() {
        super("plugins/moderation/judgementsessions");
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

    @Override
    public Class<?> getTypeClass() {
        return JudgementSession.class;
    }
}
