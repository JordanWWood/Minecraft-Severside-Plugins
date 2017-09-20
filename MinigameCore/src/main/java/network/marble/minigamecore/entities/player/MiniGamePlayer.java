package network.marble.minigamecore.entities.player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.minigamecore.MiniGameCore;

public class MiniGamePlayer {
	@Getter
	public final PlayerType playerType;

	/***
	 * Player's Mojang Id
	 */
	@Getter
	public final UUID id;

	/***
	 * Player's Marble Network Id
	 */
	public final Future<UUID> userId;

	public MiniGamePlayer (Player player){
		this(player, PlayerType.SPECTATOR);
	}

	public MiniGamePlayer (Player player, PlayerType playerType) {
		this(player.getUniqueId(), playerType);
	}

	public MiniGamePlayer (UUID id, PlayerType playerType) {
		this.id = id;
		this.playerType = playerType;
		this.userId = new CompletableFuture<UUID>();
		retrieveMiniGamePlayer();
	}

	private void retrieveMiniGamePlayer() {
		Executors.newCachedThreadPool().submit(() -> {
			try {
				User u = new User().getByUUID(id);
				((CompletableFuture<UUID>)userId).complete(u.getId());
			} catch (APIException e) {
				MiniGameCore.logger.severe("Failed to retrieve player");
				((CompletableFuture<UUID>)userId).completeExceptionally(e);
			}
		});
	}

	public Player getPlayer(){
		return Bukkit.getPlayer(id);
	}

	public UUID getUserId() {
		try {
			return userId.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			MiniGameCore.logger.severe("Failed to get user id from mg player object");
			return null;
		}
	}
}
