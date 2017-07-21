package network.marble.dataaccesslayer.consumers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
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
            String message = new String(body, "UTF-8");
            UUID id = UUID.fromString(message);
            if (id != null && CacheManager.getInstance().getCache().containsKey(id)) CacheManager.getInstance().getCache().remove(id);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}