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

    private final String EXCHANGENAME = "plugins";

    public void startQueueConsumer() {
        Channel channel = getChannel();
        if (channel == null) {
            MiniGameCore.logger.severe("Failed to start message Rabbit consumer");
            return;
        }
        if (consumer == null) consumer = new RabbitConsumer(channel);
        try {
            channel.basicConsume( getQueueName(), true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopQueueConsumer() {
        if (globalChannel != null) try {
            globalChannel.queueDelete(getQueueName());
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
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-expires", MiniGameCore.TTLMillis+(30000));
            channel.queueDeclare(getQueueName(), false, false, true, args);

            String[] keys = new String[2];
            keys[0] = "mg.server."+MiniGameCore.instanceId;
            keys[1] = "mg.server.all";
            for (String bindingKey : keys) {
                channel.queueBind(getQueueName(), EXCHANGENAME, bindingKey);
            }
            
            MiniGameCore.logger.info("mg.server."+MiniGameCore.instanceId);
            globalChannel = channel;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return globalChannel;
    }
    
    private String getQueueName() {
		return "mg." + MiniGameCore.instanceId;
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

    public <T extends Message> void respondToMessage(T message) {
    	if (message.respondTo != null && !message.respondTo.isEmpty()) sendMessage(message.respondTo, message);
    }

    public <T extends Message> void sendMessageToServer(T message) {
        String id = (MiniGameCore.instanceId == null ? new UUID(0L, 0L) : MiniGameCore.instanceId).toString();
        sendMessage("atlas.mg."+id, message);
        MiniGameCore.logger.info("MESSAGING KEY FOR SERVER" + "atlas.mg."+id);
    }

    public <T extends Message> void sendMessage(String routingkey, T message) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-id", message.getId());
        headers.put("server-id", MiniGameCore.getInstanceId().toString());
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(headers).build();
        Channel channel = getChannel();
        if (channel == null) {
            MiniGameCore.logger.severe("Failed to send Rabbit message");
            return;
        }
        try {
            channel.basicPublish(EXCHANGENAME, routingkey, properties, message.getBodyBytes());
            MiniGameCore.logger.info("PUBLISHED MESSAGE ID " + message.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RabbitManager getInstance() {
        if (instance == null) instance = new RabbitManager();
        return instance;
    }
}
