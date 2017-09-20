package network.marble.minigamecore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.message.CancelExpectedPlayerEvent;
import network.marble.minigamecore.entities.events.message.GameSetEvent;
import network.marble.minigamecore.entities.events.message.PingEvent;
import network.marble.minigamecore.entities.events.message.UnknownMessageEvent;
import network.marble.minigamecore.entities.messages.CrashReportMessage;
import network.marble.minigamecore.entities.messages.ExpectPlayersMessage;
import network.marble.minigamecore.entities.messages.PlayerExpectationCancelledMessage;
import network.marble.minigamecore.entities.messages.PongMessage;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerExpectationManager;

public class MessageEvents implements Listener {

    @EventHandler
    public void onPing(PingEvent event) {
        new PongMessage().sendToCreation();
    }

    @EventHandler
    public void onGameSet(GameSetEvent event) {
    	if(GameManager.getCurrentMiniGame() == null){
	        boolean result = GameManager.getInstance(false).setCurrentGame(event.message.uuid);
	        if(!result){
		        	CrashReportMessage message = new CrashReportMessage();
		            message.serverId = MiniGameCore.instanceId;
		            message.sendToServer();
		            MiniGameCore.logger.info("RESULT OF GAME SET FALSE, REBOOTING...");
		            Bukkit.getServer().spigot().restart();

//	            	RabbitManager.getInstance().stopQueueConsumer();
//	            	MiniGameCore.instanceId = UUID.randomUUID();
//	            	ServerAvailableMessage readyMessage = new ServerAvailableMessage();
//	            	readyMessage.serverId = MiniGameCore.instanceId;
//	            	RabbitManager.getInstance().startQueueConsumer();
//	            	readyMessage.sendToCreation();
	        }
    	}
    }

<<<<<<< Updated upstream
    /*@EventHandler
    public void onExpectedPlayer(ExpectPlayersMessage event) {
        
    }*/

=======
>>>>>>> Stashed changes
    @EventHandler
    public void onCancelExpectedPlayer(CancelExpectedPlayerEvent event) {
        PlayerExpectationManager.removePrePlayerRank(event.message.uuid);
        PlayerExpectationCancelledMessage message = new PlayerExpectationCancelledMessage(event.message.uuid, false);
        message.sendToServer();
    }

    @EventHandler
    public void onUnknownMessage(UnknownMessageEvent event) {
        MiniGameCore.logger.severe("Unknown Message, body:" + event.body);
    }
}
