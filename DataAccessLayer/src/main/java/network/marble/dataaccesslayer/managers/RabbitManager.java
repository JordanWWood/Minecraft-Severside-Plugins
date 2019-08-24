package network.marble.dataaccesslayer.managers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import lombok.NonNull;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.consumers.RabbitConsumer;
import network.marble.dataaccesslayer.entities.rabbit.RabbitResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabbitManager {
    private static RabbitManager instance;
    private Connection connection;

    RabbitManager() {
        try {
            connection = DataAccessLayer.instance.getRabbitMQConnection();
        } catch (KeyManagementException | TimeoutException | NoSuchAlgorithmException | IOException | URISyntaxException e) {
            DataAccessLayer.instance.logger.severe("Rabbit Connection error: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public RabbitResult getChannel(@NonNull String exchangeName, String exchangeType, String... routingKeys) {
        try {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, exchangeType);
            String queueName = channel.queueDeclare().getQueue();
            for (String routingKey : routingKeys) {
                channel.queueBind(queueName, exchangeName, routingKey);
            }
            return new RabbitResult(channel, queueName);
        } catch (IOException e) {
            DataAccessLayer.instance.logger.severe("Rabbit getChannel error: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static RabbitManager getInstance() {
        if (instance == null) instance = new RabbitManager();
        return instance;
    }
}
