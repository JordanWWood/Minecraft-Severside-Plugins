package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import network.marble.dataaccesslayer.models.base.BaseModel;

import java.util.UUID;

public enum CaseOutcome {
    Undecided(0),
    Muted(1),
    Kicked(2),
    TemporaryBan(3),
    PermanentlyBan(4);

    private final int value;

    CaseOutcome(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }
}