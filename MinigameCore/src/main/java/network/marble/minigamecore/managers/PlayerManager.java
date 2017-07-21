package network.marble.minigamecore.managers;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;

public class PlayerManager {
	private static PlayerManager instance;
	
	private static HashMap<UUID, MiniGamePlayer> players = new HashMap<>();

	public static Collection<MiniGamePlayer> getPlayers() {
		return players.values();
	}

	public static Collection<MiniGamePlayer> getPlayers(PlayerType type) {
		return players.values().stream().filter(p -> p.playerType == type).collect(Collectors.toList());
	}

	public static void assignPlayerTraits(Player player) {
		MiniGamePlayer miniGamePlayer = getPlayer(player.getUniqueId());
		if (miniGamePlayer != null) switch (miniGamePlayer.playerType) {
			case ADMINSTRATOR:
			case MODERATOR:
				player.setGameMode(GameMode.CREATIVE);
				break;
			case SPECTATOR:
				player.setGameMode(GameMode.SPECTATOR);
				break;
			case PLAYER:
			default:
				player.setGameMode(GameMode.SURVIVAL);
				break;
		}
	}

	public static MiniGamePlayer registerPlayer(Player player) {
		return registerPlayer(player.getUniqueId(), PlayerType.PLAYER);
	}

	public static MiniGamePlayer registerPlayer(UUID id) {
		return registerPlayer(id, PlayerType.PLAYER);
	}

	public static MiniGamePlayer registerPlayer(Player player, PlayerType type) {
		return registerPlayer(player.getUniqueId(), type);
	}

	public static MiniGamePlayer registerPlayer(UUID id, PlayerType type) {
		if (!players.containsKey(id)) players.put(id, new MiniGamePlayer(id, type));
		return players.get(id);
	}

	public static void unregisterPlayer(Player player) {
		unregisterPlayer(player.getUniqueId());
	}

	public static void unregisterPlayer(UUID id) {
		if (players.containsKey(id)) players.remove(id);
	}

	public static MiniGamePlayer getPlayer(Player player){
		return getPlayer(player.getUniqueId());
	}

	public static MiniGamePlayer getPlayer(UUID id) {
		if (players.containsKey(id)) return players.get(id);
		return null;
	}

	public String getPlayersList() {
		String list = "";
		for(Map.Entry<UUID, MiniGamePlayer> entry : players.entrySet()) {
			Player p = entry.getValue().getPlayer();
			list += entry.getValue().playerType + "-" + (p == null ? "NULL Player" : p.getDisplayName())+",";
		}
		list += " :"+players.size();
		return list;
	}
	
	public static PlayerManager getInstance() {
		if (instance == null) instance = new PlayerManager();
		return instance;
	}
	
	public static int getPlayerCount(PlayerType type) {
		if (type == null) return players.size();
		int total = 0;
		for(Entry<UUID, MiniGamePlayer> player : players.entrySet()){
			if (player.getValue().playerType == type) total++;
		}
		return total;
	}
}
