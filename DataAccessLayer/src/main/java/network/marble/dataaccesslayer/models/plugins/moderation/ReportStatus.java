package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportStatus {
    NEW,
    OPEN,
    PENDING,
    SOLVED,
    CLOSED;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}

