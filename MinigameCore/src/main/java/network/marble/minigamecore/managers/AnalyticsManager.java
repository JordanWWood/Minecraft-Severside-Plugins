package network.marble.minigamecore.managers;

import java.util.HashMap;
import java.util.UUID;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.dataaccesslayer.models.user.UserAnalytic;
import network.marble.minigamecore.MiniGameCore;

public class AnalyticsManager {
	private static AnalyticsManager instance;
	private HashMap<UUID, UserAnalytic> userAnalyticsCache = new HashMap<>();

	public void saveBufferedAnalytics(UUID id) {
		try {
			UserAnalytic ua = userAnalyticsCache.get(id);
			if (ua != null) ua.save();
			else MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
		} catch (APIException e) {
			MiniGameCore.logger.severe("Failed to save analytic for player "+id+" due to: "+e.getMessage());
		}
	}

	public void saveBufferedAnalytics() {
		userAnalyticsCache.forEach((id, analytics) -> {
			if (analytics != null) try {
				analytics.save();
			} catch (APIException e) {
				MiniGameCore.logger.severe("Failed to save analytic for player "+id+" due to: "+e.getMessage());
			} else MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
		});
		userAnalyticsCache.clear();
	}

	/***
	 * This method prefix the identifier with gameId.gameModeId.
	 * @param id is the user id not the player uuid
	 */
	public long getGameModeAnalyticsValue(UUID id, String identifier) {
		GameMode gm = GameManager.getGameMode();
		return getAnalyticsValue(id, gm.getGame_id() +"."+ gm.getId()+"."+identifier);
	}

	/**
	 * @param id is the user id not the player uuid
	 */
	public long getAnalyticsValue(UUID id, String identifier) {
		UserAnalytic ua = userAnalyticsCache.get(id);
		if (ua != null) {
			return ua.analytics.getNode(identifier).getValue();
		} else {
			MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
			return 0;
		}
	}

	/***
	 * This method prefix the identifier with gameId.gameModeId.
	 * @param id is the user id not the player uuid
	 */
	public boolean alterGameModeAnalyticsValue(UUID id, String identifier, long amount) {
		GameMode gm = GameManager.getGameMode();
		return alterAnalyticsValue(id, gm.getGame_id() +"."+ gm.getId()+"."+identifier, amount);
	}

	/**
	 * @param id is the user id not the player uuid
	 */
	public boolean alterAnalyticsValue(UUID id, String identifier, long amount) {
		UserAnalytic ua = userAnalyticsCache.get(id);
		if (ua != null) {
			return ua.analytics.alterNode(identifier, amount);
		} else {
			MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
			return false;
		}
	}

	/***
	 * This method prefix the identifier with gameId.gameModeId.
	 * @param id is the user id not the player uuid
	 */
	public boolean setGameModeAnalyticsValue(UUID id, String identifier, long amount) {
		GameMode gm = GameManager.getGameMode();
		return setAnalyticsValue(id, gm.getGame_id() +"."+ gm.getId()+"."+identifier, amount);
	}

	/**
	 * @param id is the user id not the player uuid
	 */
	public boolean setAnalyticsValue(UUID id, String identifier, long amount) {
		UserAnalytic ua = userAnalyticsCache.get(id);
		if (ua != null) {
			return ua.analytics.setNode(identifier, amount);
		} else {
			MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
			return false;
		}
	}

	public void bufferAnalyticsForPlayer(UUID id) {
		try {
			UserAnalytic ua = new UserAnalytic().get(id);
			if (ua != null) userAnalyticsCache.put(id, ua);
			else MiniGameCore.logger.severe("Failed to load analytics for player:"+id);
		} catch (APIException e) {
			MiniGameCore.logger.severe("Failed to load analytics for player due to: "+e.getMessage());
		}
	}

	public void clearAnalyticsForPlayer(UUID id) {
		MiniGameCore.logger.info("Clearing stats for "+id);
		if (userAnalyticsCache.containsKey(id)) userAnalyticsCache.remove(id);
	}
	
	public static AnalyticsManager getInstance(){
		if (instance == null) instance = new AnalyticsManager();
		return instance;
	}
}
