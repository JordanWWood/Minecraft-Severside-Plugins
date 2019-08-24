package network.marble.minigamecore.entities.messages;

import java.util.UUID;

public class CrashReportMessage extends Message {
    //Sent through the old routingkey directly
    public UUID serverId;

    public CrashReportMessage() {
        super(220);
    }
}
