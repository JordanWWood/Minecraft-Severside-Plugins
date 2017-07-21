package network.marble.dataaccesslayer.models.plugins.moderation;


import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.models.base.BaseModel;
import java.util.UUID;

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

    @Override
    public String toString() {
        return "Report{" +
                "followup_id=" + followup_id +
                ", requestee_id=" + requestee_id +
                ", accused_id=" + accused_id +
                ", assignee_id=" + assignee_id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", created_at=" + created_at +
                ", assigned_at=" + assigned_at +
                ", solved_at=" + solved_at +
                ", closed_at=" + closed_at +
                "} " + super.toString();
    }
}
