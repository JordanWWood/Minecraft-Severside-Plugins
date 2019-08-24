package network.marble.moderation.messages;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import network.marble.moderation.Moderation;
import network.marble.moderation.punishment.communication.RabbitManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatMessage {
    @Getter @Setter private UUID uuid;
    @Getter @Setter private String message;

    public void sendMessage(int messageId, String key) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-id", messageId);
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(headers).build();
        Channel channel = RabbitManager.getChannel();
        if (channel == null) {
            Moderation.getInstance().getLogger().info("Failed to send Rabbit message");
            return;
        }

        try {
            channel.basicPublish(RabbitManager.EXCHANGENAME, key, properties, new Gson().toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
