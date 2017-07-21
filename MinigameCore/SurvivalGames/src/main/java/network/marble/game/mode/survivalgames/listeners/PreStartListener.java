package network.marble.game.mode.survivalgames.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import network.marble.scoreboards.entities.scoreboard.Scoreboard;

public class PreStartListener implements Listener {
	public static Scoreboard countDown;
	public static UUID timer;
    // Stop players from moving whilst in pregame
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Vector from = new Vector(e.getFrom().toVector().getX(), 0, e.getFrom().toVector().getZ());
        Vector to = new Vector(e.getTo().toVector().getX(), 0, e.getTo().toVector().getZ());

        if (!from.equals(to))
            e.setCancelled(true);
    }
    //TODO fix leaving
    
    @EventHandler
    public void onKick(PlayerKickEvent e) {
    	killPlayer(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	killPlayer(e.getPlayer().getUniqueId());
    }
    
    private void killPlayer(UUID p){
    	if(GameListener.alivePlayers.remove(p)){
    		GameListener.deadPlayers.add(p);
    	}
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onBurn(EntityCombustEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
    	e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }
}
