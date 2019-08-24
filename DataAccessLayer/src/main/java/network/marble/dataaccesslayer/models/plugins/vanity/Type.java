package network.marble.dataaccesslayer.models.plugins.vanity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {
    PLUGIN,
    PURE,
    HYBRID;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
