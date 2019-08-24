package network.marble.minigamecore.managers;

import java.util.HashMap;
import java.util.UUID;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.dataaccesslayer.models.user.UserAnalytic;
import network.marble.hecate.Hecate;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.analytics.game.AnalyticEvent;
import network.marble.minigamecore.entities.analytics.server.ServerEvent;

public class AnalyticsManager {
	private static AnalyticsManager instance;
	private HashMap<UUID, UserAnalytic> userAnalyticsCache = new HashMap<>();

	private UUID serverInstanceUUID;

	private AnalyticsManager() {
		String name = Hecate.getServerName().replace("MINIGAME_","");
		serverInstanceUUID = Hecate.devNetFlagSet || name.equals("null") ? UUID.randomUUID() : UUID.fromString(name);

	}


	@Deprecated
	public void saveBufferedAnalytics(UUID id) {
		try {
			UserAnalytic ua = userAnalyticsCache.get(id);
			if (ua != null) ua.save();
			else MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
		} catch (APIException e) {
			MiniGameCore.logger.severe("Failed to save analytic for player "+id+" due to: "+e.getMessage());
		}
	}

	@Deprecated
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
	@Deprecated
	public long getGameModeAnalyticsValue(UUID id, String identifier) {
		GameMode gm = GameManager.getGameMode();
		return getAnalyticsValue(id, gm.getGame_id() +"."+ gm.getId()+"."+identifier);
	}

	/**
	 * @param id is the user id not the player uuid
	 */
	@Deprecated
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
	@Deprecated
	public boolean alterGameModeAnalyticsValue(UUID id, String identifier, long amount) {
		GameMode gm = GameManager.getGameMode();
		return alterAnalyticsValue(id, gm.getGame_id() +"."+ gm.getId()+"."+identifier, amount);
	}

	/**
	 * @param id is the user id not the player uuid
	 */
	@Deprecated
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
	@Deprecated
	public boolean setGameModeAnalyticsValue(UUID id, String identifier, long amount) {
		GameMode gm = GameManager.getGameMode();
		return setAnalyticsValue(id, gm.getGame_id() +"."+ gm.getId()+"."+identifier, amount);
	}

	/**
	 * @param id is the user id not the player uuid
	 */
	@Deprecated
	public boolean setAnalyticsValue(UUID id, String identifier, long amount) {
		UserAnalytic ua = userAnalyticsCache.get(id);
		if (ua != null) {
			return ua.analytics.setNode(identifier, amount);
		} else {
			MiniGameCore.logger.severe("Failed to find analytics for player:"+id);
			return false;
		}
	}

	@Deprecated
	public void bufferAnalyticsForPlayer(UUID id) {
		try {
			UserAnalytic ua = new UserAnalytic().get(id);
			if (ua != null) userAnalyticsCache.put(id, ua);
			else MiniGameCore.logger.severe("Failed to load analytics for player:"+id);
		} catch (APIException e) {
			MiniGameCore.logger.severe("Failed to load analytics for player due to: "+e.getMessage());
		}
	}

	@Deprecated
	public void clearAnalyticsForPlayer(UUID id) {
		MiniGameCore.logger.info("Clearing stats for "+id);
		if (userAnalyticsCache.containsKey(id)) userAnalyticsCache.remove(id);
	}

	// new code

	public <T extends AnalyticEvent> void submitAnalyticEvent(T analyticsEvent) {
		if (analyticsEvent == null) throw new NullPointerException("analyticsEvent == null");
		analyticsEvent.setGameId(GameManager.getGameId());
		analyticsEvent.setGameModeId(GameManager.getGameMode().getId());
		submitServerEvent(analyticsEvent);
	}

	public <T extends ServerEvent> void submitServerEvent(T serverEvent) {
		if (serverEvent == null) throw new NullPointerException("serverEvent == null");
		serverEvent.setInstanceId(serverInstanceUUID);
		network.marble.dataaccesslayer.managers.AnalyticsManager.getInstance().addEvent(serverEvent);
	}
	
	public static AnalyticsManager getInstance(){
		if (instance == null) instance = new AnalyticsManager();
		return instance;
	}
}
