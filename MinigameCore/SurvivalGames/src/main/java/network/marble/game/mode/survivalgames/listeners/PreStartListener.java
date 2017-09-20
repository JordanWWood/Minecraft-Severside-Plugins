package network.marble.game.mode.survivalgames.listeners;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.util.Vector;

import network.marble.minigamecore.entities.events.player.PlayerDisconnectEvent;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.scoreboards.entities.scoreboard.Scoreboard;

public class PreStartListener implements Listener {
	public static Scoreboard countDown;
	public static UUID timer;
    // Stop players from moving whilst in pregame
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Vector from = new Vector(e.getFrom().toVector().getX(), 0, e.getFrom().toVector().getZ());
        Vector to = new Vector(e.getTo().toVector().getX(), 0, e.getTo().toVector().getZ());

        if (!from.equals(to) && PlayerManager.getPlayer(e.getPlayer()).playerType == PlayerType.PLAYER && e.getPlayer().getGameMode() == GameMode.SURVIVAL)
            e.setCancelled(true);
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
    	if(!GameListener.deadPlayers.contains(e.getMiniGamePlayer().id))
    		GameListener.killPlayer(e.getMiniGamePlayer().getPlayer(), e.getMiniGamePlayer().playerType, false);
    	GameListener.deadPlayers.remove(e.getMiniGamePlayer().id);
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
    public void onBoat(VehicleCreateEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onRide(VehicleEnterEvent e) {
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
