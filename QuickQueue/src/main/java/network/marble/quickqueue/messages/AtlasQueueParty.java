package network.marble.quickqueue.messages;

import com.google.gson.Gson;

public class AtlasQueueParty extends Message{

    public AtlasQueueParty(GameJoinData data) {
        super(Type.ATLAS_QUEUE_PARTY.typeID, new Gson().toJson(data, GameJoinData.class), "atlas.new");
    }
}
