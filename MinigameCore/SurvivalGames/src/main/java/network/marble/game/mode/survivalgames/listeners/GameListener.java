package network.marble.game.mode.survivalgames.listeners;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import network.marble.game.mode.survivalgames.config.Model.Locations;
import network.marble.game.mode.survivalgames.events.DeathmatchStartEvent;
import network.marble.game.mode.survivalgames.managers.LootManager;
import network.marble.game.mode.survivalgames.utils.FontFormat;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.EventManager;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.TimerManager;
import network.marble.scoreboards.entities.scoreboard.Scoreboard;
import network.marble.scoreboards.entities.scoreboard.row.TextRow;
import network.marble.scoreboards.managers.ScoreboardManager;

@SuppressWarnings("deprecation")
public class GameListener implements Listener {
    private static boolean isDeathmatch = false;
    private static boolean startedDeathmatch = false;
    private static UUID timer, lightningRingTimer;
    
    //Player tracking
    public static ArrayList<UUID> deadPlayers = new ArrayList<>();
    public static ArrayList<UUID> alivePlayers = new ArrayList<>();
    public static Player winner;
    
    public static UUID playingCount, watchingCount;//Player rows
    public static Scoreboard gameBoard, deathmatchCountIn, deathmatch, winnerBoard;//Scoreboards
    public static UUID deathmatchClock, playersLeftAlive, playersNotAlive;
    @EventHandler
    public void onDeath(EntityDamageEvent e) {
    	if(e.getEntity().getType() == EntityType.PLAYER){
    		Player p = (Player)e.getEntity();
    		if(p.getHealth() - e.getFinalDamage() <= 0){
	            e.setCancelled(true);
	            killPlayer(p, PlayerManager.getPlayer(p).playerType);
	            deadPlayerPacketEffect(p);
    		}
    	}
    }
    
    private void deadPlayerPacketEffect(Player deadPlayer){
    	MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) deadPlayer.getWorld()).getHandle();
       
        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, ((CraftPlayer)deadPlayer).getHandle().getProfile(), new PlayerInteractManager(nmsWorld));
       
        npc.setLocation(deadPlayer.getLocation().getX(), deadPlayer.getLocation().getY(), deadPlayer.getLocation().getZ(), deadPlayer.getLocation().getYaw(), deadPlayer.getLocation().getPitch());
        for(Player p : Bukkit.getOnlinePlayers()){
        	if(!p.getUniqueId().equals(deadPlayer.getUniqueId())){
        		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                connection.sendPacket(new PacketPlayOutEntityStatus(npc, (byte)3));
        	}
        }
    }
    
    private void killPlayer(Player p, PlayerType type){
    	if(type == PlayerType.PLAYER){
	    	p.getWorld().strikeLightningEffect(p.getLocation());
	        for (ItemStack item : p.getInventory()) {
	            if (item == null) continue;
	            p.getWorld().dropItem(p.getLocation(), item);
	        }
	        p.getInventory().clear();
	        p.setGameMode(GameMode.SPECTATOR);
	        
	        alivePlayers.remove(p.getUniqueId());
	        deadPlayers.add(p.getUniqueId());
	        
	        Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix()+"&2A cannon can be heard in the distance..."));
	        Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix()+"[&6"+ alivePlayers.size() + "&8] &2players remain!"));
    	}
    	if(!startedDeathmatch){
	    	((TextRow)gameBoard.getRow(playingCount)).setText("Alive: " + GameListener.alivePlayers.size());
	    	((TextRow)gameBoard.getRow(watchingCount)).setText("Watching: " + (GameListener.deadPlayers.size() + PlayerManager.getPlayers(PlayerType.SPECTATOR).size()));
	    	ScoreboardManager.getInstance().updateScoreboard();
    	}else{
    		((TextRow)deathmatch.getRow(playersLeftAlive)).setText("Alive: " + GameListener.alivePlayers.size());
    		((TextRow)deathmatch.getRow(playersNotAlive)).setText("Watching: " + (GameListener.deadPlayers.size() + PlayerManager.getPlayers(PlayerType.SPECTATOR).size()));
    	}
    	
        if(alivePlayers.size() == 1){
        	winner = Bukkit.getPlayer(alivePlayers.get(0));
        	GameManager.setStatus(GameStatus.FINISHED);
        	if(timer != null) TimerManager.getInstance().stopTimer(timer);
        }else if(alivePlayers.size() <= 3 && !isDeathmatch) {
            isDeathmatch = true;
            ((TextRow)deathmatchCountIn.getRow(deathmatchClock)).setText("00:60");
            ScoreboardManager.getInstance().setScoreboard(deathmatchCountIn);
            timer = TimerManager.getInstance().runEveryUntil((t, b) -> {
        		long timeRemianing = t.executeUntil - System.currentTimeMillis();
        		((TextRow)deathmatchCountIn.getRow(deathmatchClock)).setText(String.format("%1$tM:%1$tS", timeRemianing));
        		if(!b){
        			int timeSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeRemianing);
        			if(timeSeconds == 60 || timeSeconds == 30 || timeSeconds == 15 || timeSeconds == 10 || timeSeconds <=5)
        				Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix()+"[&6" + (TimeUnit.MILLISECONDS.toSeconds(timeRemianing)) + "&8] &2seconds until deathmatch!"));
        		}else{
        			Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
        				((TextRow)deathmatchCountIn.getRow(deathmatchClock)).setText("00:10");
        				ScoreboardManager.getInstance().setScoreboard(deathmatchCountIn);
        		    	ScoreboardManager.getInstance().updateScoreboard();
        			});
        			DeathmatchStartEvent event = new DeathmatchStartEvent();
                    Bukkit.getServer().getPluginManager().callEvent(event);
        		}
            }, 1L, TimeUnit.SECONDS, 62L, TimeUnit.SECONDS);//TODO Temporarily like this due to timer skip bug
        }
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent e) {
    	killPlayer(e.getPlayer(), PlayerManager.getPlayer(e.getPlayer()).playerType);
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {//TODO null on leave
    	killPlayer(e.getPlayer(), PlayerManager.getPlayer(e.getPlayer()).playerType);
    }
	@EventHandler
    public void onFlint(BlockIgniteEvent e) {
    	if(e.getCause() == IgniteCause.FLINT_AND_STEEL){
    		short durability = (short) (e.getPlayer().getItemInHand().getDurability() + (short)20);
    		e.getPlayer().getItemInHand().setDurability(durability);
    	}
    }
    @EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
    	if(e.getBlock().getType() != Material.FIRE){
    		e.setCancelled(true);
    	}
    }

    @EventHandler
    public void onUnArt(HangingBreakByEntityEvent e) {
    	e.setCancelled(true);
    }
    
	@EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onChestOpen(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() == Material.DISPENSER){
        	e.setCancelled(true);
        	e.getPlayer().closeInventory();
        	return;
        }
        if (e.getClickedBlock().getType() == Material.CHEST) {
        	if(e.getPlayer().getGameMode() == GameMode.SPECTATOR){
        		e.setCancelled(true);
        		return;
        	}
            Location location = e.getClickedBlock().getLocation();
            Chest chest = (Chest) e.getClickedBlock().getState();

            for (Locations loc : StateListener.getMapConfig().getChests().getLocations()) {
                Location location1 = new Location(e.getPlayer().getWorld(), loc.getX(), loc.getY(), loc.getZ());

                if (location.equals(location1)) {
                    LootManager.rollLoot(loc, 6, 10, chest.getInventory().getSize() == 54, e.getPlayer().getWorld());
                    return;
                }
            }
            
            chest.getInventory().clear();
        }
    }

	@EventHandler
    public void onRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(StateListener.mapConfig.getPodiumLocation(1));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.LEAVES) return;
        if (e.getBlock().getType() == Material.LEAVES_2) return;
        if (e.getBlock().getType() == Material.LONG_GRASS) return;
        if (e.getBlock().getType() == Material.VINE) return;

        e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
    	Player source = e.getPlayer();
    	sendMessage(StateListener.mapConfig.getChat().getChatDistance(), e.getMessage(), source.getDisplayName(), source.getLocation());
    	e.setCancelled(true);
    }
    
    public static void sendMessage(int radius, String message, String sender, Location source){
    	int maxDistanceSquared = radius * radius;
    	for(MiniGamePlayer mp : PlayerManager.getPlayers(PlayerType.PLAYER)){
    		Player p = mp.getPlayer();
    		if((p.getLocation().distanceSquared(source) < maxDistanceSquared)){
    			p.sendMessage("<"+sender+ChatColor.RESET+">" + message);
    		}
    	}
    	for(MiniGamePlayer mp : PlayerManager.getPlayers(PlayerType.ADMINSTRATOR)){
    		mp.getPlayer().sendMessage("<"+sender+ChatColor.RESET+">" + message);
    	}
    	for(MiniGamePlayer mp : PlayerManager.getPlayers(PlayerType.MODERATOR)){
    		mp.getPlayer().sendMessage("<"+sender+ChatColor.RESET+">" + message);
    	}
    }

    @EventHandler
    public void onDeathmatchStart(DeathmatchStartEvent e) {
        Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
        	int i = 0;
        	for (UUID alive : alivePlayers) {
        		if(alive == null){
        			alivePlayers.remove(alive);
        			continue;
        		}
	        	Player p = Bukkit.getPlayer(alive);
	        	if(p != null){
		            p.teleport(StateListener.lookAt((new Location(p.getWorld(),
		                    StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getX(),
		                    StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getY(),
		                    StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getZ()
		            )), p.getWorld().getSpawnLocation()));
		            i++;
	        	}else{//safety catch
	        		alivePlayers.remove(alive);
	        		deadPlayers.add(alive);
	        	}
	        }
        });
        
        
        for(UUID dead : deadPlayers){
        	Player p = Bukkit.getPlayer(dead);
        	p.teleport(Bukkit.getWorld(StateListener.getCurrentGameWorld()).getSpawnLocation());
        }
        for(MiniGamePlayer p : PlayerManager.getPlayers(PlayerType.SPECTATOR)){
        	p.getPlayer().teleport(Bukkit.getWorld(StateListener.getCurrentGameWorld()).getSpawnLocation());
        }
        
        EventManager.getInstance().unregisterEvent(StateListener.getGameListener());
        EventManager.getInstance().registerEvent(StateListener.getPreStartListener());
        
        TimerManager.getInstance().runEveryUntil((t, b) -> {
    		long timeRemianing = t.executeUntil - System.currentTimeMillis();
    		((TextRow)deathmatchCountIn.getRow(deathmatchClock)).setText(String.format("%1$tM:%1$tS", timeRemianing));
    		ScoreboardManager.getInstance().updateScoreboard();
    		if(!b){
    			Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix()+"[&6" + (TimeUnit.MILLISECONDS.toSeconds(timeRemianing)) + "&8] &2seconds until deathmatch begins!"));
    		}else{
    			Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
    				playersLeftAlive = deathmatch.addRow(new TextRow("Alive: " + alivePlayers.size()));
    				playersNotAlive = deathmatch.addRow(new TextRow("Watching:" + alivePlayers.size()));
    				ScoreboardManager.getInstance().setScoreboard(deathmatch);
    				startedDeathmatch = true;
    			});
    			Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix()+"&2Deathmatch has begun!"));
                EventManager.getInstance().unregisterEvent(StateListener.getPreStartListener());
                EventManager.getInstance().registerEvent(StateListener.getGameListener());
    		}
    	}, 1L, TimeUnit.SECONDS, 12L, TimeUnit.SECONDS);//TODO Temporarily like this due to timer skip bug
        
        
        lightningRingTimer = TimerManager.getInstance().runEvery((t, b) -> {
        	if (DeathmatchListener.getCurrentDamageRadius() <= 0) TimerManager.getInstance().stopTimer(lightningRingTimer);
            DeathmatchListener.setCurrentDamageRadius(DeathmatchListener.getCurrentDamageRadius() - 1);
    	}, 10L, TimeUnit.SECONDS);

        StateListener.setDeathmatchListener(new DeathmatchListener());
        EventManager.getInstance().registerEvent(StateListener.getDeathmatchListener());
    }
}