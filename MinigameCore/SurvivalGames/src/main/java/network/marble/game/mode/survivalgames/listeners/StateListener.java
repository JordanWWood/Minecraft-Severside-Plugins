package network.marble.game.mode.survivalgames.listeners;

import java.io.FileReader;
import java.io.Reader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import network.marble.game.mode.survivalgames.commands.Vote;
import network.marble.game.mode.survivalgames.commands.Whisper;
import network.marble.game.mode.survivalgames.config.MapConfig;
import network.marble.game.mode.survivalgames.events.DeathmatchStartEvent;
import network.marble.game.mode.survivalgames.managers.LootManager;
import network.marble.game.mode.survivalgames.managers.VoteManager;
import network.marble.game.mode.survivalgames.utils.FontFormat;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.messagelibrary.api.MessageLibrary;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.game.GameStatusChangeEvent;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.AnalyticsManager;
import network.marble.minigamecore.managers.CommandManager;
import network.marble.minigamecore.managers.EventManager;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.TimerManager;
import network.marble.minigamecore.managers.WorldManager;
import network.marble.scoreboards.entities.scoreboard.Scoreboard;
import network.marble.scoreboards.entities.scoreboard.row.SpacerRow;
import network.marble.scoreboards.entities.scoreboard.row.TextRow;
import network.marble.scoreboards.managers.ScoreboardManager;

public class StateListener implements Listener {
    @Getter static VoteManager voteManager;
    @Getter static MapConfig mapConfig;
    @Getter @Setter static String currentGameWorld;

    // Listeners
    @Getter static LobbyListener lobbyListener;
    @Getter static PreStartListener preStartListener;
    @Getter static GameListener gameListener;
    @Getter @Setter static DeathmatchListener deathmatchListener;

    // Commands
    static Vote vote;
    static Whisper whisper;
    
    @Getter static final String prefix = "&8[&6MCSG&8] ";
    private static String scoreboardTitle = ChatColor.GOLD +""+ ChatColor.BOLD + "Survival Games";
    @Getter private static long startTime;

    @EventHandler
    public void onStateChangeEvent(GameStatusChangeEvent e) {
        switch (e.newStatus) {
            case INITIALIZING: initialInit(); break;
            case LOBBYING: lobbyingInit(); break;
            case PRESTART: preStartInit(); break;
            case INGAME: inGameInit(); break;
            case FINISHED: finishedInit(); break;
            case ENDED: endedInit(); break;
        }
    }

    private void initialInit() {
    	WorldManager.getInstance(false).loadWorld("survivalgameslobby");
        voteManager = new VoteManager();
        EventManager.getInstance().registerEvent(new AlwaysListener());
        StateListener.setCurrentGameWorld("survivalgameslobby");
        GameManager.setStatus(GameStatus.LOBBYING);
    }

    private void lobbyingInit() {
    	LobbyListener.waiting = new Scoreboard(scoreboardTitle);
        LobbyListener.countDown = new Scoreboard(scoreboardTitle);
        
    	TextRow timerTitle = new TextRow(ChatColor.GOLD +""+ ChatColor.BOLD+"Starting In:");
    	LobbyListener.countDown.addRows(new SpacerRow(), timerTitle);
    	TextRow waiting = new TextRow("Awaiting players...");
    	LobbyListener.waiting.addRows(new SpacerRow(), timerTitle, waiting, new SpacerRow());
    	
    	TextRow timer = new TextRow("02:00");
    	LobbyListener.countDownTimeRow = LobbyListener.countDown.addRow(timer);
    	LobbyListener.countDown.addRow(new SpacerRow());
    	
    	TextRow playersTitle = new TextRow(ChatColor.GOLD +""+ ChatColor.BOLD+"Players:");
    	LobbyListener.waiting.addRow(playersTitle);
    	LobbyListener.countDown.addRow(playersTitle);
    	
    	TextRow players = new TextRow("1/" + GameManager.getGameMode().maxPlayerCount);
    	LobbyListener.playerCountWaitingRow = LobbyListener.waiting.addRow(players);
    	LobbyListener.playerCountCountDownRow = LobbyListener.countDown.addRow(players);
    	ScoreboardManager.getInstance().setScoreboard(LobbyListener.waiting);
    	
        // Register the events
    	if(lobbyListener == null){
    		lobbyListener = new LobbyListener();
        	EventManager.getInstance().registerEvent(lobbyListener);
    	}
        vote = new Vote("Vote");
        
        CommandManager.getInstance().registerCommand(vote);
    }

	private void preStartInit() {//TODO TRACK DOWN RACE CONDITION//ALIVEPLAYERS IS NULL UNEXPECTEDLY SOME TIMES//MAY BE ORDER OF STUFF
		TimerManager.getInstance().stopTimer(LobbyListener.timer);
		
		PreStartListener.countDown = new Scoreboard(scoreboardTitle);
		PreStartListener.countDown.addRows(new SpacerRow(), new TextRow(ChatColor.GOLD+""+ChatColor.BOLD+"Starting in"));
		PreStartListener.timer = PreStartListener.countDown.addRow(new TextRow("Soon..."));
		ScoreboardManager.getInstance().setScoreboard(PreStartListener.countDown);
		
		String votedMap = VoteManager.getInstance().getMostVoted().getName();
		TimerManager.getInstance().runIn((t, b) -> {
        	Bukkit.broadcastMessage(FontFormat.translateString(prefix + "&2Chests have been refilled!"));
        	LootManager.refillChests();
        }, 15, TimeUnit.MINUTES);
		
    	InventoryAPI.disableMenus();
        // Unregister events from previous stage
        
        CommandManager.getInstance().unregisterCommand(vote);//TODO Check why this is broken

        WorldManager.getInstance(false).loadWorld(votedMap);
        StateListener.setCurrentGameWorld(votedMap);
        World world = Bukkit.getWorld(currentGameWorld);
        world.setDifficulty(Difficulty.NORMAL);
        world.setPVP(true);
        world.setTime(8000);//8 AM
        world.setAutoSave(false);
        world.setStorm(false);
        world.setThundering(false);
        
        Bukkit.getServer().setSpawnRadius(0);
        
        // Load world config
        try(Reader reader = new FileReader((Bukkit.getWorld(currentGameWorld)).getWorldFolder() + "/config.json")){
            mapConfig = new Gson().fromJson(reader, MapConfig.class);
        } catch (Exception e){
            MiniGameCore.instance.getLogger().severe("Config for " + Bukkit.getWorld(currentGameWorld).getName() + " could not be loaded. " +
                    "Double check a config has been created and tiering has been complete");
            // Tell minigames core to clean up
            e.printStackTrace();
        }
        
        
        
        for(Player p : Bukkit.getOnlinePlayers()){
        	TextComponent start = new TextComponent(FontFormat.translateString(prefix + ChatColor.GOLD + "" + mapConfig.getInfo().getName() + ChatColor.DARK_GREEN + " by "));
        	TextComponent link = new TextComponent(ChatColor.GOLD + mapConfig.getInfo().getAuthor());
        	link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, mapConfig.getInfo().getLink()));
        	link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Visit their website!").create()));
        	TextComponent end = new TextComponent(ChatColor.DARK_GREEN + " has been selected! Game will begin in 5 seconds.");
        	end.setColor(ChatColor.DARK_GREEN);
        	start.addExtra(link);
        	start.addExtra(end);
        	p.spigot().sendMessage(start);
        }
        
        
        TimerManager.getInstance().runIn((t, b) -> {
        	//Register events
        	if(preStartListener == null){
            	preStartListener = new PreStartListener();
            	EventManager.getInstance().unregisterEvent(lobbyListener);
            	EventManager.getInstance().registerEvent(preStartListener);
            }
        	
        	for(MiniGamePlayer player : PlayerManager.getPlayers(PlayerType.PLAYER)){
            	GameListener.alivePlayers.add(player.id);
            }
        	
        	 // Switch world
        	Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
	            int i = 0;
	            for (UUID p : GameListener.alivePlayers) {
	            	Player player = Bukkit.getPlayer(p);
	            	if(player != null){
		            	player.setGameMode(org.bukkit.GameMode.SURVIVAL);
		            	player.getInventory().clear();
		                player.teleport(mapConfig.getPodiumLocation(i));
		                i++;
		                if(i == mapConfig.getSpawns().getPrimary().size()) i = 0;
	            	}else{//safety catch
	            		GameListener.alivePlayers.remove(p);
	            		GameListener.deadPlayers.add(p);
	            	}
	            }
	            for (MiniGamePlayer p : PlayerManager.getPlayers(PlayerType.SPECTATOR)) {
	                p.getPlayer().teleport(mapConfig.getPodiumLocation(0));
	            }
        	});
        }, 5L, TimeUnit.SECONDS);
        
        TimerManager.getInstance().runIn((t, b) -> {
	        TimerManager.getInstance().runEveryUntil((t1, b1) -> {
	    		long timeRemianing = t1.executeUntil - System.currentTimeMillis();
	    		((TextRow)PreStartListener.countDown.getRow(PreStartListener.timer)).setText(String.format("%1$tM:%1$tS", timeRemianing));
	    		ScoreboardManager.getInstance().updateScoreboard();
	    		if(!b1){
	    			Bukkit.broadcastMessage(FontFormat.translateString(prefix+"[&6" + (TimeUnit.MILLISECONDS.toSeconds(timeRemianing)) + "&8] &2seconds until the games begin!"));
	    		}else{
	    			Bukkit.getScheduler().runTask(MiniGameCore.instance, ()->{
	    				GameManager.setStatus(GameStatus.INGAME);
	        			Bukkit.broadcastMessage(FontFormat.translateString(prefix+"&6May the odds be ever in your favour!"));
	        			startTime = System.currentTimeMillis();
	        			for(MiniGamePlayer p : PlayerManager.getPlayers(PlayerType.PLAYER)){
	        				AnalyticsManager.getInstance().alterGameModeAnalyticsValue(p.getUserId(), StateListener.mapConfig.getInfo().getName() + ".plays", 1);
	        	        	AnalyticsManager.getInstance().alterGameModeAnalyticsValue(p.getUserId(), "plays", 1);
	        			}
	    			});
	    		}
	    	}, 1L, TimeUnit.SECONDS, 12L, TimeUnit.SECONDS);//TODO Temporarily like this due to timer skip bug
        }, 8L, TimeUnit.SECONDS);
    }
	
	public static UUID gameTimer;
    private void inGameInit() {
    	whisper = new Whisper("Whisper");
        CommandManager.getInstance().registerCommand(whisper);
    	
    	GameListener.gameBoard = new Scoreboard(scoreboardTitle);
    	
    	GameListener.gameBoard.addRows(new SpacerRow(), new TextRow(ChatColor.GOLD + "" + ChatColor.BOLD + "Time Remaining:"));
    	TextRow timer = new TextRow(ChatColor.GREEN + "" + ChatColor.BOLD + "30:00");
    	UUID timerRow = GameListener.gameBoard.addRow(timer);
    	
    	
    	GameListener.gameBoard.addRows(new SpacerRow(), new TextRow(ChatColor.GOLD+""+ChatColor.BOLD+"Players"));
    	GameListener.playingCount = GameListener.gameBoard.addRow(new TextRow("Alive: " + GameListener.alivePlayers.size()));
    	GameListener.watchingCount = GameListener.gameBoard.addRow(new TextRow("Watching: " + (GameListener.deadPlayers.size() + PlayerManager.getPlayers(PlayerType.SPECTATOR).size())));
    	
    	gameTimer = TimerManager.getInstance().runEveryUntil((t, b) -> {
    		if(!b){
    			long timeRemianing = t.executeUntil - System.currentTimeMillis();
    			String colour = (timeRemianing > 1000 * 60 * 10 ? ChatColor.GREEN : timeRemianing > 1000 * 10 ? ChatColor.YELLOW : ChatColor.DARK_RED) + "";
    			((TextRow)GameListener.gameBoard.getRow(timerRow)).setText(colour + ChatColor.BOLD + String.format("%1$tM:%1$tS", timeRemianing));
    			int timeSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeRemianing);
    			if(timeSeconds == 60 || timeSeconds == 30 || timeSeconds == 15 || timeSeconds == 10 || timeSeconds <=5)
    				Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix()+"[&6" + (TimeUnit.MILLISECONDS.toSeconds(timeRemianing)) + "&8] &2seconds until deathmatch!"));
    			
    			ScoreboardManager.getInstance().updateScoreboard();
    		}else{
    			DeathmatchStartEvent event = new DeathmatchStartEvent();
                Bukkit.getServer().getPluginManager().callEvent(event);
    		}
    	}, 1L, TimeUnit.SECONDS, (30L*60L) + 1L, TimeUnit.SECONDS);
    	ScoreboardManager.getInstance().setScoreboard(GameListener.gameBoard);
    	
    	Bukkit.getWorld(currentGameWorld).setDifficulty(Difficulty.NORMAL);
        EventManager.getInstance().unregisterEvent(preStartListener);
        
        if(gameListener == null){
        	gameListener = new GameListener();
        	EventManager.getInstance().registerEvent(gameListener);
        }
        
        GameListener.deathmatchCountIn = new Scoreboard(scoreboardTitle);
        GameListener.deathmatchCountIn.addRows(new SpacerRow(), new TextRow(ChatColor.GOLD+""+ChatColor.BOLD+"Deathmatch in"));
        GameListener.deathmatchClock = GameListener.deathmatchCountIn.addRow(new TextRow("00:10"));
        
        GameListener.deathmatch = new Scoreboard(scoreboardTitle);
        GameListener.deathmatch.addRows(new SpacerRow(), new TextRow(ChatColor.GOLD+""+ChatColor.BOLD+"Players remaining"));
    }
    
    private void finishedInit() {
    	if(gameTimer != null) TimerManager.getInstance().stopTimer(gameTimer);
    	EventManager.getInstance().registerEvent(new EndListener());
    	Scoreboard winnerBoard = new Scoreboard(scoreboardTitle);
    	winnerBoard.addRows(new SpacerRow(), new TextRow(ChatColor.GOLD+""+ChatColor.BOLD + "Winner"), new TextRow(FontFormat.translateString(MessageLibrary.getDisplayName(GameListener.winner.getUniqueId()))));
    	ScoreboardManager.getInstance().setScoreboard(winnerBoard);
    	
        EventManager.getInstance().unregisterEvent(gameListener);
        EventManager.getInstance().unregisterEvent(deathmatchListener);
        Bukkit.broadcastMessage(FontFormat.translateString(prefix + ChatColor.GOLD + GameListener.winner.getDisplayName() + "&2 has won the survival games!"));
        TimerManager.getInstance().runIn((t, b) -> {
        	Bukkit.getScheduler().scheduleSyncDelayedTask(MiniGameCore.instance, ()->{
        		for(Player p : Bukkit.getOnlinePlayers()){
        			p.kickPlayer("Back to the hub.");
        		}
        	});
        	GameManager.setStatus(GameStatus.ENDED);
        }, 10, TimeUnit.SECONDS);
    }

    private void endedInit() {

    }
}
