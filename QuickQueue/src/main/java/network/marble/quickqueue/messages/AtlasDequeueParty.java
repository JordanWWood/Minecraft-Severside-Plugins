package network.marble.quickqueue.messages;

import com.google.gson.Gson;

public class AtlasDequeueParty extends Message{

    public AtlasDequeueParty(GameLeaveData data) {
        super(Type.ATLAS_DEQUEUE_PARTY.typeID, new Gson().toJson(data, GameLeaveData.class), "atlas.new");
    }
}
