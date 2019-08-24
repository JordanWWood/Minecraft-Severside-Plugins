package network.marble.dataaccesslayer.models.plugins.moderation;


import com.fasterxml.jackson.annotation.JsonValue;

public enum CaseOutcome {
    Undecided,
    Muted,
    Kicked,
    TemporaryBan,
    PermanentlyBan,
    IpMuted,
    Warn;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}