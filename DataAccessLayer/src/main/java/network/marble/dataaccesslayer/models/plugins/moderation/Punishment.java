package network.marble.dataaccesslayer.models.plugins.moderation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Punishment extends BaseModel<Punishment> {
    private String offense;
    private String description;
    private String conditions;
    private String examples;
    private List<RepeatedAction> repeat_actions;

    public Punishment() {
        super("plugins/moderation/punishments");
    }

    @Override
    public Class<?> getTypeClass() {
        return Punishment.class;
    }

    @Data
    public static class RepeatedAction {
        private int order;
        private PunishmentAction action;
        private Long duration;
        private PunishmentUnit duration_time_unit;
    }

    public enum PunishmentAction {
        WARN,
        KICK,
        MUTE,
        TEMP_BAN,
        PERM_BAN
    }

    public enum PunishmentUnit {
        SECOND(1000L),
        MINUTE(60000L),
        HOUR(3600000L),
        DAY(86400000L),
        WEEK(604800000L),
        MONTH(2628000000L),
        YEAR(31536000000L);

        @Getter private Long time;

        PunishmentUnit(Long time) {
            this.time = time;
        }
    }
}
