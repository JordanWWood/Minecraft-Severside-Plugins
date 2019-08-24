package network.marble.dataaccesslayer.managers;

import lombok.NonNull;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.common.Context;
import network.marble.dataaccesslayer.entities.ChatSession;

import java.util.UUID;

public class ChatManager {
    public static ChatSession getJudgementChatSession(@NonNull UUID sessionId) {
        SocketManager m = new SocketManager();
        Context context = Context.getInstance();
        final String path = context.getBaseUrl()+"plugins/moderation/judgementsessions/chatsession?sessionId="+sessionId;
        try {
            return new ChatSession(m.getNewSignalRConnection(path));
        } catch (Exception e) {
            DataAccessLayer.instance.logger.severe("Failed to create signal r connection: "+e.getMessage());
            return null;
        }
    }
}
