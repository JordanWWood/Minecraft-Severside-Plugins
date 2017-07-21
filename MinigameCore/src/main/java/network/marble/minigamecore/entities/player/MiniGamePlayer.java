package network.marble.minigamecore.entities.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MiniGamePlayer {
	public final PlayerType playerType;
	public final UUID id;
	
	public MiniGamePlayer (Player player){
		this(player, PlayerType.SPECTATOR);
	}
	
	public MiniGamePlayer (Player player, PlayerType playerType) {
		this(player.getUniqueId(), playerType);
	}

	public MiniGamePlayer (UUID id, PlayerType playerType) {
		this.id = id;
		this.playerType = playerType;
	}

	public Player getPlayer(){
		return Bukkit.getPlayer(id);
	}
}
