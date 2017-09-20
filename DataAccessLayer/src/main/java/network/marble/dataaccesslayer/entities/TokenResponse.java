package network.marble.dataaccesslayer.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@ToString
public class TokenResponse {

    @Getter
    @SerializedName("access_token")
    private String accessToken;

    @Getter
    @SerializedName("expires_in")
    private int expiresIn;

    @Getter
    @SerializedName("token_type")
    private String tokenType;

    @Getter
    @SerializedName("error")
    private String error;
}
