package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Report extends BaseModel<Report> {
    public Report(){
        super("plugins/moderation/reports", "reports", "report");
    }

    @Getter @Setter
    public UUID followup_id;

    @Getter @Setter
    public UUID requestee_id;

    @Getter @Setter
    public UUID accused_id;

    @Getter @Setter
    public UUID assignee_id;

    @Getter @Setter
    public String description;

    @Getter @Setter
    public ReportStatus status;

    @Getter @Setter
    public long created_at;

    @Getter @Setter
    public long assigned_at;

    @Getter @Setter
    public long solved_at;

    @Getter @Setter
    public long closed_at;

    @Override
    public Class<?> getTypeClass() {
        return Report.class;
    }
}
