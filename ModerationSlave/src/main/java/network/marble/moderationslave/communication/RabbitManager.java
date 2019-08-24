package network.marble.moderationslave.communication;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import network.marble.dataaccesslayer.bukkit.DataAccessLayer;
import network.marble.hecate.Hecate;
import network.marble.moderationslave.ModerationSlave;

import java.io.IOException;
import java.util.UUID;

public class RabbitManager {
    private static Consumer consumer;
    private static Connection connection;
    private static Channel globalChannel;
    private static String queueName = "mslave." + Hecate.getServerName();
    public static final String EXCHANGENAME = "moderation";

    public static void startQueueConsumer() {
        try {
            connection = DataAccessLayer.getInstance().getRabbitMQConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Channel channel = getChannel();
        if (channel == null) {
            ModerationSlave.getInstance().getLogger().info("Failed to start Rabbit consumer");
            return;
        }
        if (consumer == null) consumer = new RabbitConsumer(channel);
        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Channel getChannel() {
        if (globalChannel == null || !globalChannel.isOpen()) try {
            if (connection == null) {
                ModerationSlave.getInstance().getLogger().info("Failed to retrieve Rabbit connection");
                return null;
            }
            Channel channel = connection.createChannel();
            if (channel == null) {
                ModerationSlave.getInstance().getLogger().info("Failed to create Rabbit Channel");
                return null;
            }

            channel.exchangeDeclare(EXCHANGENAME, "topic");
            channel.queueDeclare(queueName, false, true, true, null);

            channel.queueBind(queueName, EXCHANGENAME, "mod." + Hecate.getServerName());

            globalChannel = channel;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return globalChannel;
    }

    public static void bindKeyForUser(UUID uuid) {
        try {
            globalChannel.queueBind(queueName, EXCHANGENAME, "mslave.player." + uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bindKeyForUser(String name) {
        try {
            globalChannel.queueBind(queueName, EXCHANGENAME, "mslave.player." + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unBindKeyForUser(UUID uuid) {
        try {
            globalChannel.queueUnbind(queueName, EXCHANGENAME, "mslave.player." + uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unBindKeyForUser(String name) {
        try {
            globalChannel.queueUnbind(queueName, EXCHANGENAME, "mslave.player." + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
