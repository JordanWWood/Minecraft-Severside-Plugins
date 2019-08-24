package network.marble.dataaccesslayer.entities;

import com.microsoft.aspnet.signalr.HubConnection;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.models.plugins.moderation.Log;

import java.util.UUID;

public class ChatSession {
    private final HubConnection hubConnection;

    public ChatSession(HubConnection hubConnection) {
        this.hubConnection = hubConnection;
    }

    public Observable<Log> IncommingMessage() {
        return PublishSubject.create(emitter -> {
            hubConnection.on("ReceiveMessage", (user, message, timeCode) -> {
                Log log = new Log();
                log.setUser_id(user);
                log.setMessage(message);
                log.setTimestamp(timeCode);
                emitter.onNext(log);
            }, UUID.class, String.class, Long.class);

            hubConnection.onClosed(e -> {
                DataAccessLayer.instance.logger.info("Chat Session Closed");
                emitter.onComplete();
            });

            hubConnection.start();
            DataAccessLayer.instance.logger.info("Chat Session Opened");
        });
    }

    public void SendMessage(Log log) {
        try {
            hubConnection.send("SendMessage", log.getUser_id(), log.getMessage());
        } catch (Exception e) {
            DataAccessLayer.instance.logger.info("Send message failed due to: " + e.getMessage());
        }
    }
}
