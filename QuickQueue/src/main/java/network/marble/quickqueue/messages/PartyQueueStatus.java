package network.marble.quickqueue.messages;

import com.google.gson.Gson;

public class PartyQueueStatus extends Message{

    public PartyQueueStatus(PartyQueueData data) {
        super(Type.ATLAS_QUEUE_STATUS.typeID, new Gson().toJson(data, PartyQueueData.class), "atlas.new");
    }
}
