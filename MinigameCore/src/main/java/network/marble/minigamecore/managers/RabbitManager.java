package network.marble.minigamecore.managers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import network.marble.dataaccesslayer.bukkit.DataAccessLayer;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.consumers.RabbitConsumer;
import network.marble.minigamecore.entities.messages.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

public class RabbitManager {
    private static RabbitManager instance;

    private Channel globalChannel;
    static Consumer consumer;

    private String queueName;
    private final String EXCHANGENAME = "plugins";

    public RabbitManager() {
        queueName = "mg." + MiniGameCore.instanceId.toString();
    }

    public void startQueueConsumer() {
        Channel channel = getChannel();
        if (channel == null) {
            MiniGameCore.logger.severe("Failed to start message Rabbit consumer");
            return;
        }
        if (consumer == null) consumer = new RabbitConsumer(channel);
        try {
            channel.basicConsume( queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopQueueConsumer() {
        if (globalChannel != null) try {
            globalChannel.queueDelete(queueName);
            globalChannel.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel() {
        if (globalChannel == null || !globalChannel.isOpen()) try {
            Connection connection = DataAccessLayer.getInstance().getRabbitMQConnection();
            if (connection == null) {
                MiniGameCore.logger.severe("Failed to retrieve Rabbit connection");
                return null;
            }
            Channel channel = connection.createChannel();
            if (channel == null) {
                MiniGameCore.logger.severe("Failed to create Rabbit Channel");
                return null;
            }
            channel.exchangeDeclare(EXCHANGENAME, "topic");
            channel.queueDeclare(queueName, false, true, false , null);

            String[] keys = new String[3];
            keys[0] = "mg";
            keys[1] = "server";
            keys[2] = MiniGameCore.instanceId.toString();
            for (String bindingKey : keys) {
                channel.queueBind(queueName, EXCHANGENAME, bindingKey);
            }
            globalChannel = channel;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return globalChannel;
    }
    
    public <T extends Message> void sendMessageUndocumented(T message, String key) {
        sendMessage("atlas."+key, message);
    }

    public <T extends Message> void sendMessageToError(T message) {
        sendMessage("atlas.errors", message);
    }

    public <T extends Message> void sendMessageToCreation(T message) {
        sendMessage("atlas.new", message);
    }

    public <T extends Message> void respondToMessage(T message, T respondToMessage) {
        if (respondToMessage.respondTo != null && !respondToMessage.respondTo.isEmpty()) sendMessage(respondToMessage.respondTo, message);
    }

    public <T extends Message> void sendMessageToServer(T message) {
        String id = (MiniGameCore.instanceId == null ? new UUID(0L, 0L) : MiniGameCore.instanceId).toString();
        sendMessage("atlas.mg."+id, message);
    }

    public <T extends Message> void sendMessage(String routingkey, T message) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-type", message.getId());
        headers.put("server-id", MiniGameCore.getInstanceId().toString());
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(headers).build();
        Channel channel = getChannel();
        if (channel == null) {
            MiniGameCore.logger.severe("Failed to send Rabbit message");
            return;
        }
        try {
            channel.basicPublish(EXCHANGENAME, routingkey, properties, message.getBodyBytes());
            Bukkit.getLogger().info("PUBLISHED MESSAGE ID " + message.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RabbitManager getInstance() {
        if (instance == null) instance = new RabbitManager();
        return instance;
    }
}
