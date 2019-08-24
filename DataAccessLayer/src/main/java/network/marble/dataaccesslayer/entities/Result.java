package network.marble.dataaccesslayer.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Result {
    @Getter
    @JsonProperty("generated_keys")
    private List<UUID> generatedKeys;

    @Getter
    @JsonProperty("inserted")
    private int inserted;

    @Getter
    @JsonProperty("replaced")
    private int replaced;

    @Getter
    @JsonProperty("unchanged")
    private int unchanged;

    @Getter
    @JsonProperty("errors")
    private int errors;

    @Getter
    @JsonProperty("first_error")
    private String firstError;

    @Getter
    @JsonProperty("deleted")
    private int deleted;

    @Getter
    @JsonProperty("skipped")
    private int skipped;

    @Getter
    @JsonProperty("warnings")
    private String warnings;

    @Getter
    @JsonProperty("changes")
    private String changes;

    @Getter
    @JsonProperty("ready")
    private String ready;
}
