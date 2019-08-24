package network.marble.moderationslave.communication;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messageapi.api.MessageAPI;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.commands.PunishmentCommand;
import network.marble.moderationslave.communication.messages.PunishmentRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class RabbitConsumer extends DefaultConsumer {
    public RabbitConsumer(Channel channel) {
        super(channel);
    }

    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            // In a testing environment whilst sending messages manually the message-id appears to be serialized into a Long
            // whilst during production it appears to be an int. Whilst I am not sure why this happens this try catch is
            // a bodge
            int messageType = 0;
            try {
                messageType = (int) properties.getHeaders().getOrDefault("message-id", 0);
            } catch (ClassCastException e) {
                messageType = ((Long) properties.getHeaders().getOrDefault("message-id", 0)).intValue();
            }

            String message = new String(body, "UTF-8");
            Gson g = new Gson();

            switch (messageType) {
                case 151: {
                    PunishmentRequest r = g.fromJson(message, PunishmentRequest.class);
                    Player player = Bukkit.getPlayer(r.getName());
                    if (player == null) {
                        MessageAPI.sendMessage(null, r.getSender(), "mod.cross.limbo.fail", true);
                        return;
                    }

                    User u = new User().get(r.getSender());
                    PunishmentCommand.limboPlayer(player, u, r.getCaseId());
                } break;
                default:
                    ModerationSlave.getInstance().getLogger().warning("ModerationSlave received an unknown message with the id " + messageType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
