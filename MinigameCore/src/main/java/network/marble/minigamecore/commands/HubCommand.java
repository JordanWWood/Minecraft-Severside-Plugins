package network.marble.minigamecore.commands;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.menus.quitgame.QuitGameMenu;
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
		this.commandAliases = Arrays.asList("leave", "quit", "kermitsudoku");
		this.canBeRunBy = Arrays.asList(PlayerType.ADMINISTRATOR, PlayerType.MODERATOR, PlayerType.PLAYER, PlayerType.SPECTATOR);
		this.setDescription("Hub Command for Minigame Core");
	}

	@Override
	public boolean commandExecution(CommandSender sender, String label, String[] args) {
		MiniGamePlayer mg = PlayerManager.getPlayer((Player)sender);
		if (mg.playerType == PlayerType.PLAYER && GameManager.getStatus() == GameStatus.INGAME)
			InventoryAPI.openMenuForPlayer(mg.id, new QuitGameMenu(mg.getPlayer()));
		else
			mg.getPlayer().kickPlayer("Hub Command");
		return true;
	}
}
