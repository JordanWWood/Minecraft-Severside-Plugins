package network.marble.quickqueue.managers;

import lombok.Getter;
import lombok.ToString;

@ToString
public class PartyResult {
    @Getter private final boolean success;
    @Getter private final String response;

    public PartyResult(boolean success, String response){
        this.success = success;
        this.response = response;
    }
}
