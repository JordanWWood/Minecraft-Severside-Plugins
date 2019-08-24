package network.marble.moderationslave.communication.messages;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.communication.RabbitManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PunishmentRequest {
    private String name;
    private String category;
    private UUID sender;
    private UUID caseId;

    public void sendMessage(String key) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-id", 151);
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(headers).build();
        Channel channel = RabbitManager.getChannel();
        if (channel == null) {
            ModerationSlave.getInstance().getLogger().info("Failed to send Rabbit message");
            return;
        }

        try {
            channel.basicPublish(RabbitManager.EXCHANGENAME, key, properties, new Gson().toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
