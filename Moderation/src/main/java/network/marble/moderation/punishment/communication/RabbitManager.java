package network.marble.moderation.punishment.communication;


import com.rabbitmq.client.*;

import network.marble.dataaccesslayer.bungee.DataAccessLayer;
import network.marble.hermes.Hermes;
import network.marble.moderation.Moderation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RabbitManager {
    static Consumer consumer;
    private static Connection connection;
    private static Channel globalChannel;
    private static String queueName = "mod." + Hermes.getBungeeID();
    public static final String EXCHANGENAME = "moderation";

    private Map<UUID, String> playerQueue = new HashMap<>();

    public static void startQueueConsumer() {
        try {
            connection = DataAccessLayer.getInstance().getRabbitMQConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Channel channel = getChannel();
        if (channel == null) {
            Moderation.getInstance().getLogger().info("Failed to start Rabbit consumer");
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
                Moderation.getInstance().getLogger().info("Failed to retrieve Rabbit connection");
                return null;
            }
            Channel channel = connection.createChannel();
            if (channel == null) {
                Moderation.getInstance().getLogger().info("Failed to create Rabbit Channel");
                return null;
            }

            channel.exchangeDeclare(EXCHANGENAME, "topic");
            channel.queueDeclare(queueName, false, true, true, null);
            channel.queueBind(queueName, EXCHANGENAME, "mod." + Hermes.getBungeeID());

            globalChannel = channel;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return globalChannel;
    }

    public static void bindKeyForUser(UUID uuid) {
        try {
            globalChannel.queueBind(queueName, EXCHANGENAME, "mod.player." + uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bindKey(String key) {
        try {
            globalChannel.queueBind(queueName, EXCHANGENAME, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unBindKeyForUser(UUID uuid) {
        try {
            globalChannel.queueUnbind(queueName, EXCHANGENAME, "mod.player." + uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unBindKey(String key) {
        try {
            globalChannel.queueUnbind(queueName, EXCHANGENAME, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
