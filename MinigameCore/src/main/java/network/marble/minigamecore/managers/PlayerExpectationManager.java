package network.marble.minigamecore.managers;

import network.marble.minigamecore.entities.player.PlayerType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerExpectationManager {
    private static PlayerExpectationManager instance;
    private static ConcurrentHashMap<UUID, PlayerType> expectedPlayerRanks = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, UUID> cancelTaskIds = new ConcurrentHashMap<>();
    public static boolean overrideExpected = false;

    public static void addPrePlayerRank(UUID uuid, PlayerType rank) {
        expectedPlayerRanks.put(uuid, rank);
        UUID id = TimerManager.getInstance().runIn((timer, last) -> {
            if (expectedPlayerRanks.containsKey(uuid)) expectedPlayerRanks.remove(uuid);
            if (cancelTaskIds.containsKey(uuid)) cancelTaskIds.remove(uuid);
        }, 400);
        cancelTaskIds.put(uuid, id);
    }

    public static void removePrePlayerRank(UUID uuid) {
        if (expectedPlayerRanks.containsKey(uuid)) expectedPlayerRanks.remove(uuid);
        TimerManager.getInstance().stopTimer(cancelTaskIds.get(uuid));
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
        for (UUID i : cancelTaskIds.values()) {
            TimerManager.getInstance().stopTimer(i);
        }
        cancelTaskIds.clear();
    }


    public static PlayerExpectationManager getInstance() {
        if (instance == null) instance = new PlayerExpectationManager();
        return instance;
    }
}
