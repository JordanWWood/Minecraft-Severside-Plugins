package network.marble.dataaccesslayer.entities.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper = true)
public abstract class RedactiveEvent extends Event {
    public RedactiveEvent() { super(EventType.Redactive); }
}
