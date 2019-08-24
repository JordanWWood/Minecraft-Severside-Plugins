package network.marble.dataaccesslayer.entities.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper = true)
public abstract class AdditiveEvent extends Event {
    public AdditiveEvent() { super(EventType.Additive); }
}
