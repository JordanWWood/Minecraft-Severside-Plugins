package network.marble.dataaccesslayer.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
public class TokenResponse {

    @Getter
    @JsonProperty("access_token")
    private String accessToken;

    @Getter
    @JsonProperty("expires_in")
    private int expiresIn;

    @Getter
    @JsonProperty("token_type")
    private String tokenType;

    @Getter
    @JsonProperty("error")
    private String error;
}
