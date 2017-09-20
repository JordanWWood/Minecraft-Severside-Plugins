package network.marble.minigamecore.managers;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.player.PlayerType;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerExpectationManager {
    private static PlayerExpectationManager instance;
    private static ConcurrentHashMap<UUID, PlayerType> expectedPlayerRanks = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, Integer> cancelTaskIds = new ConcurrentHashMap<>();
    public static boolean overrideExpected = false;

    public static void addPrePlayerRank(UUID uuid, PlayerType rank) {
        expectedPlayerRanks.put(uuid, rank);
        int id = Bukkit.getScheduler().runTaskLaterAsynchronously(MiniGameCore.instance, () -> {
            if (expectedPlayerRanks.containsKey(uuid)) expectedPlayerRanks.remove(uuid);
            if (cancelTaskIds.containsKey(uuid)) cancelTaskIds.remove(uuid);
        }, 400).getTaskId();
        cancelTaskIds.put(uuid, id);
    }

    public static void removePrePlayerRank(UUID uuid) {
        if (expectedPlayerRanks.containsKey(uuid)) expectedPlayerRanks.remove(uuid);
        Bukkit.getScheduler().cancelTask(cancelTaskIds.get(uuid));
        if (cancelTaskIds.containsKey(uuid)) cancelTaskIds.remove(uuid);
    }

    public static PlayerType getExpectedPlayerType(UUID uuid) {
        PlayerType type =  expectedPlayerRanks.get(uuid);
        if (type == null && overrideExpected) return PlayerType.PLAYER;
        return type;
    }

    public static PlayerType pullExpectedPlayerType(UUID uuid) {
        PlayerType type = getExpectedPlayerType(uuid);
        if (expectedPlayerRanks.containsKey(uuid)) removePrePlayerRank(uuid);
        return type;
    }
    
    public static void clearExpectations() {
    	expectedPlayerRanks.clear();
    	for(Integer i : cancelTaskIds.values()){
    		Bukkit.getScheduler().cancelTask(i);
    	}
    	cancelTaskIds.clear();
    }


    public static PlayerExpectationManager getInstance() {
        if (instance == null) instance = new PlayerExpectationManager();
        return instance;
    }
}
