package network.marble.quickqueue.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

import network.marble.dataaccesslayer.bukkit.DataAccessLayer;
import network.marble.hecate.Hecate;

public abstract class Message {
    private static Connection connection;
    private static Channel globalChannel;
    String contents;
    String[] targetRoutingKeys;
    private int messageID;
    static Consumer consumer;
    private static String queueName = "quickqueue." + Hecate.getServerName();
    private static final String EXCHANGENAME = "plugins";
    
    public Message(int messageID, String contents, String... targetRoutingKey){
        this.messageID = messageID;
        this.contents = contents;
        this.targetRoutingKeys = targetRoutingKey;
    }

    public static void startQueueConsumer() {
        try {
            connection = DataAccessLayer.getInstance().getRabbitMQConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Channel channel = getChannel();
        if (channel == null) {
            System.out.println("Failed to start Rabbit consumer");
            return;
        }
        if (consumer == null) consumer = new MessageConsumer(channel);
        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Channel getChannel() {
        if (globalChannel == null || !globalChannel.isOpen()) try {
            if (connection == null) {
                System.out.println("Failed to retrieve Rabbit connection");
                return null;
            }
            Channel channel = connection.createChannel();
            if (channel == null) {
                System.out.println("Failed to create Rabbit Channel");
                return null;
            }
            channel.exchangeDeclare(EXCHANGENAME, "topic");
            channel.queueDeclare(queueName, false, true, true, null);

            channel.queueBind(queueName, EXCHANGENAME, "qq."+Hecate.serverName);
            channel.queueBind(queueName, EXCHANGENAME, "qq.*");
            
            globalChannel = channel;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return globalChannel;
    }

    public static void bindKey(String key){
        try {
            getChannel().queueBind(queueName, EXCHANGENAME, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unbindKey(String key){
        try {
            getChannel().queueUnbind(queueName, EXCHANGENAME, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(){
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-id", messageID);
        headers.put("respond-to", "qq."+Hecate.serverName);
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(headers).build();
        Channel channel = getChannel();
        if (channel == null) {
            System.out.println("Failed to send Rabbit message");
            return;
        }
        for(String targetRoutingKey : targetRoutingKeys){
            try {
                channel.basicPublish(EXCHANGENAME, targetRoutingKey, properties, contents.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

enum Type{
    UNKNOWN(0),
    PING(1),
    PONG(2),
    //Bungee unification
    PLAYER_CHANGE_SERVER(1),
    PLAYER_LEFT(2),
    MULTI_JOIN_KICK(3),
    //Party actions
    PARTY_DATA(4),
    PARTY_DATA_REQUEST(5),
    //Atlas communication
    ATLAS_QUEUE_PARTY(40),
    ATLAS_DEQUEUE_PARTY(41),
    ATLAS_QUEUE_STATUS(42),
    //Server address mounting
    SERVER_AVAILABLE(90),
    SERVER_UNAVAILABLE(91);

    public int typeID;
    private static Map<Integer, Type> map = new HashMap<>();

    static {
        for (Type type : Type.values()) {
            map.put(type.typeID, type);
        }
    }

    Type(int messageID){
        typeID = messageID;
    }

    public static Type valueOf(int typeID) {
        return map.get(typeID);
    }
}
