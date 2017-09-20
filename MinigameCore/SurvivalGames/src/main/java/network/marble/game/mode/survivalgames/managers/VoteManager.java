package network.marble.game.mode.survivalgames.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Synchronized;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.game.mode.survivalgames.config.MapConfig;
import network.marble.game.mode.survivalgames.listeners.StateListener;
import network.marble.game.mode.survivalgames.utils.FontFormat;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.world.World;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.WorldManager;

public class VoteManager {
    @Getter private static VoteManager instance;
    ArrayList<World> worlds = new ArrayList<>();
    @Getter HashMap<Integer, Integer> voteStore = new HashMap<>();
    @Getter HashMap<Integer, String> mapNames = new HashMap<>();
    
    public VoteManager() {
    	instance = this;
    	HashMap<UUID, List<World>> s = WorldManager.getInstance(false).getWorldsByGameModeId();
        List<World> minigameWorlds = s.get(GameManager.getGameMode().id);
        Collections.shuffle(minigameWorlds);

        for(int i = 0; i < minigameWorlds.size() && i < 3; i++){
        	worlds.add(minigameWorlds.get(i));
        	voteStore.put(i, 0);
        	// Load world config
            try{
                MapConfig mapConfig = new Gson().fromJson(minigameWorlds.get(i).loadFile("world/config.json"), MapConfig.class);
                mapNames.put(i, mapConfig.getInfo().getName());
            } catch (Exception e){
                MiniGameCore.instance.getLogger().severe("Config for " + minigameWorlds.get(i).getName() + " could not be loaded. " +
                        "Double check a config has been created and tiering has been complete");
                // Tell minigames core to clean up
                e.printStackTrace();
            }
        	
        }
    }
    
    public void sendVoteMessage(Player p){
    	Bukkit.getLogger().info("sending");
    	String prefix = FontFormat.translateString(StateListener.getPrefix());
    	ArrayList<BaseComponent[]> lines = new ArrayList<>();
    	for(int i = 0; i < worlds.size(); i++){
    		//[MCSG] 1 > | 0 Votes | The Survival Gaems
    		int votes = voteStore.get(i) == null ? 0 : voteStore.get(i);
    		lines.add(new ComponentBuilder(prefix).append(ChatColor.GREEN+""+(i+1) + ChatColor.DARK_GRAY + " > | " + ChatColor.YELLOW + votes + ChatColor.GRAY + " Votes " +
    				ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + mapNames.get(i)).create());
    	}
    	for(BaseComponent[] t : lines){
    		p.spigot().sendMessage(t);
    	}
    }
    
    private final Object readLock = new Object();
    
    @Synchronized("readLock")
    public boolean addVote(int pos, Rank rank, Player p) {
    	int weight = (rank == null) ? 1 : RankWeight.valueOf(rank.getName().toUpperCase()).getWeight();
        if (!voteStore.containsKey(pos))
            return false;
        
        int current = voteStore.get(pos) == null ? 0 : voteStore.get(pos);
        voteStore.put(pos, current += weight);
        sendVoteMessage(p);
        return true;
    }
    
    @Synchronized("readLock")
    public boolean removeVote(int pos, Rank rank) {
    	int weight = (rank == null) ? 1 : RankWeight.findBySimpleName(rank.getName()).getWeight();
        if (!voteStore.containsKey(pos))
            return false;

        int current = voteStore.get(pos) == null ? 0 : voteStore.get(pos);
        voteStore.replace(pos, current -= weight);
        
        return true;
    }
    
    public World getMostVoted(){
    	int mostVotes = Integer.MIN_VALUE;
    	ArrayList<Integer> mostVotedMaps = new ArrayList<>();
    	for(Entry<Integer, Integer> mapVotes : voteStore.entrySet()){
    		Bukkit.getLogger().info(mapVotes.getKey() + " - " + mapVotes.getValue());
    		if(mapVotes.getValue() >= mostVotes){
    			if(mapVotes.getValue() > mostVotes) mostVotedMaps.clear();
    			mostVotes = mapVotes.getValue();
    			mostVotedMaps.add(mapVotes.getKey());
    		}
    	}
    	
    	if(mostVotedMaps.size() > 1){
    		Bukkit.getLogger().info(">1");
    		Collections.shuffle(mostVotedMaps);
    	}else if(mostVotedMaps.size() < 1){
    		Bukkit.getLogger().info("<1");
            List<World> minigameWorlds = WorldManager.getInstance(false).getWorldsByGameModeId().get(GameManager.getGameMode().id);
            Collections.shuffle(minigameWorlds);
            return minigameWorlds.get(0);
    	}
    	Bukkit.getLogger().info("Vote winner = " +worlds.get(mostVotedMaps.get(0)).getName());
    	return worlds.get(mostVotedMaps.get(0));
    }

    public enum RankWeight {
        DEFAULT("Default", 1),
        ALPHA("Alpha", 1),
        BETA("Beta", 2),
        GAMMA("Gamma", 2),
        DELTA("Delta", 3),
        EPSILON("Epsilon", 4),
        VIP("VIP", 4),
        TEAMELITE("TeamElite", 4),
        MEDIA("Media", 4),
        HELPER("Helper", 4),
        MOD("Moderator", 4),
        SENIORMODERATOR("Senior Moderator", 4),
        ADMIN("Administrator", 4),
        DEV("Developer", 4),
        OWNER("Owner", 4);

        @Getter private String simpleName;
        @Getter private int weight;

        RankWeight(String simpleName, int weight) {
            this.simpleName = simpleName;
            this.weight = weight;
        }

        public static RankWeight findBySimpleName(String name){
            for(RankWeight rw : values()){
                if(rw.simpleName.equals(name)){
                    return rw;
                }
            }
            return null;
        }
    }
}


