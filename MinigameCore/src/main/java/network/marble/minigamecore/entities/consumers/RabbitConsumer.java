package network.marble.minigamecore.entities.consumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.LongString;

import network.marble.hecate.Hecate;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.message.CancelExpectedPlayerEvent;
import network.marble.minigamecore.entities.events.message.GameSetEvent;
import network.marble.minigamecore.entities.events.message.PingEvent;
import network.marble.minigamecore.entities.events.message.UnknownMessageEvent;
import network.marble.minigamecore.entities.messages.CancelExpectedPlayerMessage;
import network.marble.minigamecore.entities.messages.ExpectPlayersMessage;
import network.marble.minigamecore.entities.messages.GameModeSetMessage;
import network.marble.minigamecore.entities.messages.PingMessage;
import network.marble.minigamecore.entities.messages.PlayersExpectedMessage;
import network.marble.minigamecore.entities.messages.ServerAvailableMessage;
import network.marble.minigamecore.entities.messages.ServerDataMessage;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerExpectationManager;
import network.marble.minigamecore.managers.PlayerManager;

public class RabbitConsumer extends DefaultConsumer {
    public RabbitConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            int messageType = (int)properties.getHeaders().getOrDefault("message-type", 0);
            Object respondToRaw = properties.getHeaders().getOrDefault("respond-to", null);
            String respondTo = null;
            if (respondToRaw != null) {
                respondTo = new String(((LongString)respondToRaw).getBytes(), "UTF-8");
                MiniGameCore.logger.info("respondTo = " + respondTo);
            }
            String message = new String(body, "UTF-8");
            Gson g = new Gson();
            switch (messageType) {
                case 1:
                    Bukkit.getServer().getPluginManager().callEvent(new PingEvent(new PingMessage()));
                    break;
                case 48:
                    PlayerExpectationManager.clearExpectations();
                    if(GameManager.getCurrentMiniGame() == null){
                        ServerAvailableMessage sam = new ServerAvailableMessage();
                        sam.serverId = MiniGameCore.instanceId;
                        sam.sendToCreation();
                    }else{
                        ServerDataMessage sdm = new ServerDataMessage();
                        List<UUID> cL = new ArrayList<>();
                        PlayerManager.getPlayers(PlayerType.PLAYER).forEach(p -> cL.add(p.id));
                        UUID players[] = new UUID[cL.size()];
                        players = cL.toArray(players);
                        sdm.currentPlayers = players;
                        sdm.serverId = MiniGameCore.instanceId;
                        sdm.gameId = GameManager.getGameMode().id;
                        sdm.status = GameManager.getStatus();
                        sdm.sendToCreation();
                    }
                    break;
                case 60:
                    GameModeSetMessage gsm = g.fromJson(message, GameModeSetMessage.class);
                    Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> Bukkit.getServer().getPluginManager().callEvent(new GameSetEvent(gsm)));
                    break;
                case 61:
                    ExpectPlayersMessage epm = g.fromJson(message, ExpectPlayersMessage.class);
                    MiniGameCore.logger.info("Expect Player Event Start:\n"+message);
                    //TODO check max players and reject if the party wont fit
                    for (UUID uuid : epm.playerIds) {
                        PlayerExpectationManager.addPrePlayerRank(uuid, PlayerType.get(epm.type));
                    }
                    PlayersExpectedMessage pExpected = new PlayersExpectedMessage();
                    pExpected.playerID = epm.playerIds;
                    pExpected.respondTo = respondTo;
                    pExpected.serverName = Hecate.getServerName();
                    pExpected.respondTo();
                    MiniGameCore.logger.info("Expect Player Event End");
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