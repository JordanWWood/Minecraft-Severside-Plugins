package network.marble.dataaccesslayer.managers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import network.marble.dataaccesslayer.bukkit.DataAccessLayer;
import network.marble.dataaccesslayer.consumers.RabbitConsumer;

import java.io.IOException;

public class RabbitManager {
    private static RabbitManager instance;

    private Channel globalChannel;
    private static Consumer consumer;

    private final String EXCHANGENAME = "dal";
    private String queueName;

    public void startQueueConsumer() {
        Channel channel = getChannel();
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
            Channel channel = DataAccessLayer.getInstance().getRabbitMQConnection().createChannel();
            channel.exchangeDeclare(EXCHANGENAME, "direct");
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGENAME, "cache");
            globalChannel = channel;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return globalChannel;
    }

    public static RabbitManager getInstance() {
        if (instance == null) instance = new RabbitManager();
        return instance;
    }
}
