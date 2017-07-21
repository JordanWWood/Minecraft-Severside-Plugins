package network.marble.minigamecore.commands;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.ConfirmationMenu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class HubCommand extends MinigameCommand {

	public HubCommand() {
		super("hub");
		this.CANBERUNBY = Arrays.asList(PlayerType.ADMINSTRATOR, PlayerType.MODERATOR, PlayerType.PLAYER, PlayerType.SPECTATOR);
		this.setDescription("Hub Command for Minigame Core");
	}

	@Override
	public boolean commandExecution(CommandSender sender, String label, String[] args) {
		MiniGamePlayer mg = PlayerManager.getPlayer((Player)sender);
		if (mg.playerType == PlayerType.PLAYER && GameManager.getStatus() == GameStatus.INGAME) {
			ConfirmationMenu menu = new ConfirmationMenu(mg.getPlayer(), null, new ActionExecutor() {
				@Override
				public void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String[] args) {
					mg.getPlayer().kickPlayer("Hub Command");
				}
			}, "Leave Game", "Cancel");
			InventoryAPI.openMenuForPlayer(mg.id, menu);
		}
		else mg.getPlayer().kickPlayer("Hub Command");
		return true;
	}
}
