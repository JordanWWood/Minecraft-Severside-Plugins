package network.marble.dataaccesslayer.managers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import lombok.NonNull;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.consumers.RabbitConsumer;
import network.marble.dataaccesslayer.entities.rabbit.RabbitResult;

import java.io.IOException;
import java.util.UUID;

public class CacheRabbitManager {
    private static CacheRabbitManager instance;

    private Channel globalChannel;
    private static Consumer consumer;

    private final String EXCHANGENAME = "dal";
    private String queueName;

    /**
     * Starts the DAL queue consumer
     */
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
        if (globalChannel == null || !globalChannel.isOpen()) {
            RabbitResult result = RabbitManager.getInstance().getChannel(EXCHANGENAME, "topic", "cache.update.all");
            globalChannel = result.getChannel();
            queueName = result.getQueueName();
        }
        return globalChannel;
    }

    public void unbindCacheKey(UUID key) throws IOException {
        getChannel().queueUnbind(queueName, EXCHANGENAME, "cache.update."+key);
    }

    public void bindCacheKey(UUID key) throws IOException {
        getChannel().queueBind(queueName, EXCHANGENAME, "cache.update."+key);
    }

    public static CacheRabbitManager getInstance() {
        if (instance == null) instance = new CacheRabbitManager();
        return instance;
    }
}
