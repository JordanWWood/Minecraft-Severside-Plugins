package network.marble.minigamecore.managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.material.Command;
import org.bukkit.plugin.SimplePluginManager;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.command.MinigameCommand;

public class CommandManager {
	private static CommandManager instance;
	private static List<MinigameCommand> stack = new ArrayList<>();
	public static boolean restrictionOverride = MiniGameCore.devNetFlagSet;
	public static List<String> disabledCommands = Arrays.asList("kill", "pl", "?", "reload", "restart", "plugins", "op", "deop", "bukkit:", "minecraft:", "bungee", "version");

	/***
	 * Register multiple command through this method rather than with spigiot.
	 * @param commandClasses An ArrayList of the command objects extending MinigameCommand to be registered
	 */
	public void registerCommands(ArrayList<? super MinigameCommand> commandClasses) {
		registerCommands(commandClasses, false);
	}
	
	/***
	 * Register a command through this method rather than with spigiot.
	 * @param commandClasses The command object extending MinigameCommand
	 * @param ignoreStack Set to true to override the command stack that removes mingame commands on minigame unload
	 */
	public void registerCommands(ArrayList<? super MinigameCommand> commandClasses, boolean ignoreStack) {
		if(commandClasses == null) return;
		CommandMap commandMap = getCommandMap();
		Iterator<? super MinigameCommand> i = commandClasses.iterator();
		while (i.hasNext())
		{
			MinigameCommand commandClass = (MinigameCommand)i.next();
			commandClass.setAliases(commandClass.commandAliases);
			commandMap.register(commandClass.commandAliases.get(0), commandClass);
			if (!ignoreStack) stack.add(commandClass);
			MiniGameCore.logger.info("Command "+String.join(", ",commandClass.commandAliases) + " registered");
		}
	}

	/***
	 * Register a command through this method rather than with spigiot.
	 * @param command The command object extending MinigameCommand to be registered
	 */
	public <T extends MinigameCommand> void registerCommand(T command) {
		registerCommand(command, false);
	}

	/***
	 * Register a command through this method rather than with spigiot.
	 * @param command The command object extending MinigameCommand to be registered
	 * @param ignoreStack Set to true to override the command stack that removes mingame commands on minigame unload
	 */
	public <T extends MinigameCommand> void registerCommand(T command, boolean ignoreStack) {
		MinigameCommand commandClass = (MinigameCommand)command;
		commandClass.setAliases(commandClass.commandAliases);
		getCommandMap().register(commandClass.commandAliases.get(0), commandClass);
		if (!ignoreStack) stack.add(commandClass);
		MiniGameCore.logger.info("Command "+String.join(", ",commandClass.commandAliases)+" registered");
	}

	/***
	 * Unregisters an existing registered command class.
	 * @param command The command class to unregister
	 */
	public <T extends MinigameCommand> boolean unregisterCommand(T command) {
		MinigameCommand commandClass = (MinigameCommand)command;
		CommandMap commandMap = getCommandMap();
		boolean result = commandMap.getCommand(commandClass.commandAliases.get(0)).unregister(commandMap);
		commandClass.getAliases().forEach(aliase -> {
			try {
				final Field f = commandMap.getClass().getDeclaredField("knownCommands");
				f.setAccessible(true);
				@SuppressWarnings("unchecked")//TODO look for way to properly check
				Map<String, Command> cmds = (Map<String, Command>) f.get(commandMap);
				if (cmds.containsKey(aliase)) cmds.remove(aliase);
				f.set(commandMap, cmds);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		setCommandMap(commandMap);
		if (result && stack.contains(commandClass)) stack.remove(commandClass);
		MiniGameCore.logger.info("Command "+String.join(", ",commandClass.commandAliases)+" unregistered "+(result ? "successfully": "unsuccessfully"));
		return result;
	}

	/***
	 * Unregisters all registered commands.
	 */
	public void unregisterAllCommands() {
		CommandMap commandMap = getCommandMap();

		try {
			final Field f = commandMap.getClass().getDeclaredField("knownCommands");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")//TODO look for way to properly check
			Map<String, Command> cmds = (Map<String, Command>) f.get(commandMap);
			cmds.clear();
			f.set(commandMap, cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setCommandMap(commandMap);
	}

	public void clearStack() {
		stack.forEach(command -> getCommandMap().getCommand(command.commandAliases.get(0)).unregister(getCommandMap()));
		stack.clear();
	}

//	private PluginCommand getCommand(String name) {
//		PluginCommand command = null;
//
//		try {
//			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, MiniGameCore.class);
//			c.setAccessible(true);
//			command = c.newInstance(name, MiniGameCore.instance);
//		} catch (SecurityException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
//			e.printStackTrace();
//		}
//
//		return command;
//	}

	private CommandMap getCommandMap() {
		CommandMap commandMap = null;
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);

				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return commandMap;
	}

	private void setCommandMap(CommandMap map) {
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);

				f.set(Bukkit.getPluginManager(), map);
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static CommandManager getInstance(){
		if (instance == null) instance = new CommandManager();
		return instance;
	}
}
