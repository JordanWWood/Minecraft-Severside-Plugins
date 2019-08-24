package network.marble.dataaccesslayer.consumers;

import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.managers.CacheManager;

import java.io.IOException;
import java.util.UUID;

public class RabbitConsumer extends DefaultConsumer {
    public RabbitConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            UUID id = UUID.fromString(new String(body, Charsets.UTF_8));
            if (CacheManager.getInstance().containsKey(id)) CacheManager.getInstance().remove(id);
        } catch(Exception e) {
            DataAccessLayer.instance.logger.severe("Failed to process cache removal request due to: "+e.getMessage());
        }
    }
}