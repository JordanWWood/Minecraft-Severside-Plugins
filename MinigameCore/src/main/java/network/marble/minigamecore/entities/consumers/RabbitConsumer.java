package network.marble.minigamecore.entities.consumers;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import network.marble.minigamecore.entities.events.message.*;
import network.marble.minigamecore.entities.messages.CancelExpectedPlayerMessage;
import network.marble.minigamecore.entities.messages.ExpectPlayersMessage;
import network.marble.minigamecore.entities.messages.GameModeSetMessage;
import network.marble.minigamecore.entities.messages.PingMessage;
import org.bukkit.Bukkit;

import java.io.IOException;

public class RabbitConsumer extends DefaultConsumer {
    public RabbitConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            int messageType = (int)(long)properties.getHeaders().getOrDefault("message-type", 0);
            String respondTo = (String) properties.getHeaders().getOrDefault("respond-to", null);
            String message = new String(body, "UTF-8");
            Gson g = new Gson();
            switch (messageType) {
                case 1:
                    Bukkit.getServer().getPluginManager().callEvent(new PingEvent(new PingMessage()));
                    break;
                case 60:
                    GameModeSetMessage gsm = g.fromJson(message, GameModeSetMessage.class);
                    Bukkit.getServer().getPluginManager().callEvent(new GameSetEvent(gsm));
                    break;
                case 61:
                    ExpectPlayersMessage epm = g.fromJson(message, ExpectPlayersMessage.class);
                    epm.respondTo = respondTo;
                    Bukkit.getServer().getPluginManager().callEvent(new ExpectPlayersEvent(epm));
                    break;
                case 62:
                    CancelExpectedPlayerMessage cepm = g.fromJson(message, CancelExpectedPlayerMessage.class);
                    Bukkit.getServer().getPluginManager().callEvent(new CancelExpectedPlayerEvent(cepm));
                    break;
                default:
                    Bukkit.getServer().getPluginManager().callEvent(new UnknownMessageEvent(message, properties.getHeaders()));
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}