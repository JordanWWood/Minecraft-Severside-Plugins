package network.marble.dataaccesslayer.models.plugins.moderation;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivePunishmentActionType {
    Mute,
    IpMuted,
    TemporaryBan,
    PermanentlyBan;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}