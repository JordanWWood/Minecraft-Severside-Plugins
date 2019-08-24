package network.marble.dataaccesslayer.models.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface IBaseModel extends Serializable {
    UUID id = null;

    String toString();
}