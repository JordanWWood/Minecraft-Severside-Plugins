package network.marble.minigamecore.commands;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.game.MiniGame;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.entities.team.Team;
import network.marble.minigamecore.entities.world.World;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CoreCommand extends MinigameCommand {

	public CoreCommand() {
		super("mgc");
		this.canBeRunBy = Arrays.asList(PlayerType.ADMINISTRATOR);
		this.commandAliases = Arrays.asList("mgc", "minigamecore");
		this.setDescription("Core Commands for Minigame Core");
	}

	@Override
	public List<String> tabCompletion(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> returns = Arrays.asList();
		if (args.length == 1) returns = Arrays.asList("games", "game", "reload", "players", "id", "teams", "team", "scoreboard", "worlds", "world");
		else if (args.length == 2) switch(args[0]) {
			case "game":
				returns = Arrays.asList("load", "set", "list", "unload", "unset", "status");
				break;
			case "id":
				returns = Arrays.asList("list");
				break;
			case "team":
				returns = Arrays.asList("list");
				break;
			case "scoreboard":
				returns = Arrays.asList("refresh");
				break;
			case "world":
				returns = Arrays.asList("load", "set", "list", "unload", "unset", "spawn");
				break;
		} else if(args.length == 3) switch(args[0]+" "+args[1]) {
			case "game set":
			case "game load":
				returns = MiniGameCore.gameManager.getGames().stream().map(MiniGame::getName).collect(Collectors.toList());
				break;
			case "game status":
				returns = Arrays.asList("+","-");
				break;
			case "world spawn":
			case "world set":
			case "world load":
			case "world unload":
			case "world unset":
				returns = MiniGameCore.worldManager.getWorlds().stream().map(World::getName).collect(Collectors.toList());
				break;
		}
		if (args[args.length-1] != "") returns = returns.stream().filter(s -> s.startsWith(args[args.length-1])).collect(Collectors.toList());
		return returns;
	}


	@Override
	public boolean commandExecution(CommandSender sender, String label, String[] args) {
		String message = null;
		if (args.length > 0) {
			switch(args[0]) {
				case "games":
					args = new String[] {"game","list"};
					break;
				case "players":
					args = new String[] {"id","list"};
					break;
				case "teams":
					args = new String[] {"team","list"};
					break;
				case "worlds":
					args = new String[] {"world","list"};
					break;
			}

			switch(args[0]) {
				case "game":
					message = game(sender,label,args);
					break;
				case "reload":
					message = reload(sender,label,args);
					break;
				case "id":
					message = player(sender,label,args);
					break;
				case "team":
					message = team(sender,label,args);
					break;
				case "scoreboard":
					message = scoreboard(sender,label,args);
					break;
				case "world":
					message = world(sender,label,args);
					break;
			}
		} else message = "Minigame Core "+ MiniGameCore.getPlugin(MiniGameCore.class).getDescription().getVersion();
		if (message == null) message = "No command found";
		sender.sendMessage(message);
		return false;
	}

	private String game(CommandSender sender, String label, String[] args) {
		String message = null;
		if (args.length == 1) {
			MiniGame mg = GameManager.getCurrentMiniGame();
			message = mg == null ? message = "No game set": "The Current game is "+mg.getName();
		} else
			switch(args[1]) {
				case "set":
				case "load":
					UUID gameName = UUID.fromString(args[2]);
					message = GameManager.getInstance(false).setCurrentGame(gameName) ? "Game successfully changed to " + gameName : "Failed to change game to " + gameName;
					break;
				case "list":
					message = "Minigames: " + MiniGameCore.gameManager.getMiniGamesList();
					break;
				case "unset":
				case "unload":
					message = MiniGameCore.gameManager.unsetCurrentGame() ? "Game successfully unset" : "Failed to unset game";
					break;
				case "status":
					MiniGame mg = GameManager.getCurrentMiniGame();
					if (args.length == 2) {
						message = mg == null ? message = "No game set": mg.getName()+"'s status: "+ GameManager.getStatus().toString();
					} else {
						switch (args[2]) {
							case "+":
								GameManager.progressStatus();
								break;
							case "-":
								GameManager.regressStatus();
								break;
						}
						message = mg == null ? message = "No game set": mg.getName()+"'s status changed to "+ GameManager.getStatus().toString();
					}
					break;
			}
		return message;
	}

	private String reload(CommandSender sender, String label, String[] args) {
		MiniGameCore.instance.deinit();
		MiniGameCore.instance.init();
		return "Reloaded";
	}

	private String player(CommandSender sender, String label, String[] args) {
		String message = null;
		if (args.length == 1)
			message = "Your type is " + PlayerManager.getPlayer((Player)sender).playerType;
		else switch(args[1]) {
			case "list":
				message = "Players: " + MiniGameCore.playerManager.getPlayersList();
				break;
		}
		return message;
	}

	private String team(CommandSender sender, String label, String[] args) {
		String message = null;
		if (args.length > 1)
			switch(args[1]) {
				case "list":
					message = "Teams: \n";
					for (Team t: MiniGameCore.teamManager.getTeams()) {
						message += t.getTeamIdentifier() +": ";
						for (MiniGamePlayer p: t.getPlayers()) {
							if (!p.getPlayer().getUniqueId().equals(t.getPlayers().get(0).getPlayer().getUniqueId())) message += ", ";
							message += p.getPlayer().getDisplayName();
						}
						message += " :"+t.getPlayers().size()+"\n";
					}
					break;
			}
		return message;
	}

	private String scoreboard(CommandSender sender, String label, String[] args) {
		String message = null;
		if (args.length > 1)
			switch(args[1]) {
				case "refresh":
					message = "OLD COMMAND, has no fuction, Scoreboards refreshed";
					break;
			}
		return message;
	}

	private String world(CommandSender sender, String label, String[] args) {
		String message = null;
		if (args.length == 1) {
			String list = Arrays.toString(WorldManager.getCurrentWorlds().keySet().toArray());
			message = WorldManager.getCurrentWorlds().size() > 0 ? message = "No worlds set": "The current worlds are "+list;
		} else switch(args[1]) {
			case "load":
			case "set":
				message = MiniGameCore.worldManager.loadWorld(args[2]) ? "World successfully loaded " + args[2] : "Failed to load world " + args[2];
				break;
			case "list":
				message = "Worlds: " + MiniGameCore.worldManager.getWorldList();
				break;
			case "unload":
			case "unset":
				message = MiniGameCore.worldManager.unloadWorld(args[2]) ? "World successfully unloaded" : "Failed to unload world";
				break;
			case "spawn":
				World world = WorldManager.getCurrentWorlds().get(args[2]);
				if (world == null) message = "Failed to find "+args[2];
				else {
					org.bukkit.World bukkitWorld = Bukkit.getWorld(world.getName());
					if (bukkitWorld == null) message = "Bukkit world not found for "+world.getName();
					else {
						Location spawnLoc = bukkitWorld.getSpawnLocation();
						((Player) sender).teleport(spawnLoc);
						message = "You been spawned in "+world.getName();
					}
				}
				break;
			}
		return message;
	}
}
