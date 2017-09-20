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
    @JsonProperty("Inserted")
    private int inserted;

    @Getter
    @JsonProperty("Replaced")
    private int replaced;

    @Getter
    @JsonProperty("Unchanged")
    private int unchanged;

    @Getter
    @JsonProperty("Errors")
    private int errors;

    @Getter
    @JsonProperty("first_error")
    private String firstError;

    @Getter
    @JsonProperty("Deleted")
    private int deleted;

    @Getter
    @JsonProperty("Skipped")
    private int skipped;

    @Getter
    @JsonProperty("Warnings")
    private String warnings;

    @Getter
    @JsonProperty("Changes")
    private String changes;

    @Getter
    @JsonProperty("Ready")
    private String ready;
}
