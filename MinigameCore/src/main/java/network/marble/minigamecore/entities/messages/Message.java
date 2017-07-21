package network.marble.minigamecore.entities.messages;

import com.google.gson.Gson;
import lombok.Getter;
import network.marble.minigamecore.managers.RabbitManager;

public class Message {
    @Getter
    public final int id;

    public String respondTo;

    public Message(int id) {
        this.id = id;
    }

    public byte[] getBodyBytes(){
        return getBodyString().getBytes();
    }

    public String getBodyString() {
        return new Gson().toJson(this);
    }

    public <T extends Message> void respondTo(T message) {
        RabbitManager.getInstance().respondToMessage(this, message);
    }

    public void sendToServer(){
        RabbitManager.getInstance().sendMessageToServer(this);
    }

    public void sendToCreation(){
        RabbitManager.getInstance().sendMessageToCreation(this);
    }

    public void sendToErrors(){
        RabbitManager.getInstance().sendMessageToError(this);
    }
}
