package network.marble.quickqueue.messages;

import java.io.IOException;
import java.util.UUID;

import network.marble.dataaccesslayer.models.GameMode;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.Inventory;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MessageConsumer extends DefaultConsumer {
    public MessageConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            Type messageType = Type.valueOf((int)properties.getHeaders().getOrDefault("message-type", 0));
            String respondTo = properties.getHeaders().getOrDefault("respond-to", null).toString();
            String message = new String(body, "UTF-8");
            Gson g = new Gson();
            Bukkit.getLogger().info("QQ got message: \n" + message);
            Bukkit.getLogger().info("QQ got message type is: " + messageType);
            switch (messageType) {
                case ATLAS_QUEUE_STATUS:
                    PartyQueueData su = g.fromJson(message, PartyQueueData.class);
                    Party p = PartyManager.getCachedParty(su.partyId);
                    GameMode gameMode = new GameMode().get(su.gameModeId);

                    //Update the cached model
                    p.setQueued(su.isQueued);

                    //Message each player the queue status
                    for(UUID member : p.getMembersWithLeader()) {
                        Player player = Bukkit.getPlayer(member);
                        if(player != null) {
                            String outputMessage = Lang.replaceUnparsedTag(Lang.get(su.isQueued ? "qq.party.in.queue" : "qq.party.left.queue", player),
                                    "qq.atlas.queue.game", Lang.get(gameMode.getName(), player));
                            player.sendMessage(outputMessage);
                            if(member.equals(p.getLeader())){
                                PartyManager.getInstance().setPartyQueue(member, gameMode);
                            }
                            InventoryAPI.refreshPlayerView(player);
                        }
                    }
                    break;
                default: System.out.println("Unknown message recieved from " + respondTo); break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
