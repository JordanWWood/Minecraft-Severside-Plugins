package network.marble.moderation.punishment.communication;


import com.rabbitmq.client.*;

import network.marble.dataaccesslayer.bungee.DataAccessLayer;
import network.marble.moderation.Moderation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RabbitListener {
    private Connection connection = DataAccessLayer.getInstance().getRabbitMQConnection();
    private Channel channel;

    private final String EXCHANGE = "Moderation_Punishment";

    private Map<UUID, String> playerQueue = new HashMap<>();

    public RabbitListener() {
        try {
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE, "topic");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DeclareQueueForPlayer(UUID player) throws IOException {
        final String QueueName = channel.queueDeclare("Mod." + player.toString(), false, false, true, null).getQueue();

        playerQueue.put(player, QueueName);
        channel.queueBind(QueueName, EXCHANGE, "inbound." + player.toString());

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                StringBuilder player = new StringBuilder();
                boolean isUuid = false;
                for (char c: envelope.getRoutingKey().toCharArray()) {
                    if (c == '.'){
                        isUuid = true;
                        continue;
                    }

                    if (!isUuid) continue;

                    player.append(c);
                }

                String message = new String(body, "UTF-8");
                Moderation.getInstance().getLogger().info("Recived Message for " + player + ": " + message);
                Moderation.getInstance().getLogger().info("Routing key: " + envelope.getRoutingKey());

                Moderation.getPunishmentManager().sendMessageToPlayer(UUID.fromString(player.toString()), message);
            }
        };
        channel.basicConsume(QueueName, true, consumer);
    }

    public void PublishMessageForPlayer(UUID player, String message) throws IOException {
        channel.basicPublish(EXCHANGE, "outbound." + player.toString(), null, message.getBytes());
    }
}
