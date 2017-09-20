package network.marble.game.mode.survivalgames.listeners;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import network.marble.game.mode.survivalgames.managers.VoteManager;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.TimerManager;
import network.marble.scoreboards.entities.scoreboard.Scoreboard;
import network.marble.scoreboards.entities.scoreboard.row.TextRow;
import network.marble.scoreboards.managers.ScoreboardManager;

public class LobbyListener implements Listener {
	private static boolean timerGoing = false;
	public static UUID timer;
	public static UUID countDownTimeRow, playerCountWaitingRow, playerCountCountDownRow;
	public static Scoreboard waiting, countDown;
	
	@EventHandler
	public void onPlayerKicked(PlayerKickEvent e) {
		timerCheck();
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		timerCheck();
	}
	
	private void timerCheck(){
		if(PlayerManager.getPlayers(PlayerType.PLAYER).size() > 5 && !timerGoing){
			timerGoing = true;
			ScoreboardManager.getInstance().setScoreboard(countDown);
			timer = TimerManager.getInstance().runEveryUntil((t, b) -> {
	    		long timeRemaining = t.executeUntil - System.currentTimeMillis();
	    		if(b){
	    			((TextRow)countDown.getRow(countDownTimeRow)).setText(String.format("%1$tM:%1$tS", timeRemaining));
	    			ScoreboardManager.getInstance().updateScoreboard();
	    			Bukkit.getScheduler().runTask(MiniGameCore.instance, ()->{
	    				GameManager.setStatus(GameStatus.PRESTART);
	    			});
	    		}else{
	    			((TextRow)countDown.getRow(countDownTimeRow)).setText(String.format("%1$tM:%1$tS", timeRemaining));
	    			ScoreboardManager.getInstance().updateScoreboard();
	    		}
	    	}, 1L, TimeUnit.SECONDS, 121L, TimeUnit.SECONDS);//TODO Temporarily like this due to timer skip bug
        }else if(PlayerManager.getPlayers(PlayerType.PLAYER).size() <= 5 && timerGoing){
        	ScoreboardManager.getInstance().setScoreboard(waiting);
        	((TextRow)countDown.getRow(countDownTimeRow)).setText("02:00");
        	TimerManager.getInstance().stopTimer(timer);
        	timerGoing = false;
        }
		
		UUID updating = timerGoing ? playerCountCountDownRow : playerCountWaitingRow;
        ((TextRow)ScoreboardManager.getCurrentScoreboard().getRow(updating)).setText(PlayerManager.getPlayers(PlayerType.PLAYER).size() + "/" + GameManager.getGameMode().maxPlayerCount);
        ScoreboardManager.getInstance().updateScoreboard();
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setExp(0F);
        p.setGameMode(GameMode.ADVENTURE);

        p.getPlayer().getInventory().clear();
        p.teleport(Bukkit.getWorld("survivalgameslobby").getSpawnLocation());
        
        timerCheck();
        VoteManager.getInstance().sendVoteMessage(p);
    }
    
    @EventHandler
    public void onBurn(EntityCombustEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
    	e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
