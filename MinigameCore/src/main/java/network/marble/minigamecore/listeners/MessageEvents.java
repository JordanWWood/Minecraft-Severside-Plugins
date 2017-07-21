package network.marble.minigamecore.listeners;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.message.*;
import network.marble.minigamecore.entities.messages.CrashReportMessage;
import network.marble.minigamecore.entities.messages.PlayerExpectationCancelledMessage;
import network.marble.minigamecore.entities.messages.PlayersExpectedMessage;
import network.marble.minigamecore.entities.messages.PongMessage;
import network.marble.minigamecore.entities.messages.ServerAvailableMessage;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerExpectationManager;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MessageEvents implements Listener {

    @EventHandler
    public void onPing(PingEvent event) {
        new PongMessage().sendToCreation();
    }

    @EventHandler
    public void onGameSet(GameSetEvent event) {
        boolean result = GameManager.getInstance(false).setCurrentGame(event.message.uuid);
        if(!result){
        	CrashReportMessage message = new CrashReportMessage();
            message.serverId = MiniGameCore.instanceId;
            message.sendToCreation();
            MiniGameCore.instanceId = UUID.randomUUID();
            ServerAvailableMessage readyMessage = new ServerAvailableMessage();
            readyMessage.serverId = MiniGameCore.instanceId;
            readyMessage.sendToCreation();
        }
    }

    @EventHandler
    public void onExpectedPlayer(ExpectPlayersEvent event) {
        Bukkit.getLogger().info("Expect Player Event Start");
        //TODO check max players and reject if the party wont fit
        for(UUID uuid : event.message.players){
        	PlayerExpectationManager.addPrePlayerRank(uuid, event.message.type);
        }
        PlayersExpectedMessage message = new PlayersExpectedMessage();
        message.uuids = event.message.players;
        message.respondTo = "atlas.move." + event.message.leaderId;
        message.respondTo(event.message);
        Bukkit.getLogger().info("Expect Player Event End");
    }

    @EventHandler
    public void onCancelExpectedPlayer(CancelExpectedPlayerEvent event) {
        PlayerExpectationManager.removePrePlayerRank(event.message.uuid);
        PlayerExpectationCancelledMessage message = new PlayerExpectationCancelledMessage(event.message.uuid, false);
        message.sendToServer();
    }

    @EventHandler
    public void onUnknownMessage(UnknownMessageEvent event) {
        Bukkit.getLogger().severe("Unknown Message, body:" + event.body);
    }
}
