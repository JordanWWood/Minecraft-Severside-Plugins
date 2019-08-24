package network.marble.moderation.punishment.communication;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.hermes.messages.Type;
import network.marble.moderation.Moderation;
import network.marble.moderation.messages.ChatMessage;
import network.marble.moderation.punishment.PunishmentManager;
import network.marble.moderation.utils.FontFormat;

import java.io.IOException;

public class RabbitConsumer extends DefaultConsumer {

    public RabbitConsumer(Channel channel) {
        super(channel);
    }

    @Override
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

            String respondTo = properties.getHeaders().getOrDefault("respond-to", "").toString();
            String message = new String(body, "UTF-8");
            Gson g = new Gson();

            switch (messageType) {
                case 149: {
                    ChatMessage chatMessage = g.fromJson(message, ChatMessage.class);

                    User u = new User().get(chatMessage.getUuid());
                    Rank r = new Rank().get(u.getRank_id());

                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        User recipient = new User().get(p.getUniqueId());

                        if (recipient.hasPermission("mod.staff.chat"))
                            p.sendMessage(new TextComponent(ChatColor.RED + "[" + ChatColor.GOLD + "SC" + ChatColor.RED +
                                    "] " + (chatMessage.getUuid() == null ? "" : FontFormat.translateString(r.getPrefix()
                                    + u.getDisplayName() + r.getSuffix() + ":") + chatMessage.getMessage())));
                    }
                } break;
                case 150: {
                    ChatMessage chatMessage = g.fromJson(message, ChatMessage.class);
                    Moderation.getPunishmentManager().sendMessageToPlayer(chatMessage.getUuid(), chatMessage.getMessage());
                }
                break;
                default:
                    Moderation.getInstance().getLogger().warning("Moderation received an unknown message with the id " + messageType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
