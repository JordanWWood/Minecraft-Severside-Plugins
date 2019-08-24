package network.marble.dataaccesslayer.entities.analytics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

@Data
public abstract class Event implements Serializable {

    @JsonIgnore
    private static transient final long serialVersionUID = 1L;

    @Setter(AccessLevel.PUBLIC)
    private long timeCode;

    private final EventType type;

    protected Event(EventType type) {
        this.type = type;
    }
}
