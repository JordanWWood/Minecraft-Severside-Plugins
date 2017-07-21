package network.marble.minigamecore.managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.command.MinigameCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.SimplePluginManager;

public class CommandManager {
	private static CommandManager instance;
	private static List<MinigameCommand> stack = new ArrayList<>();
	public static boolean restrictionOverride = false;
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
			commandClass.setAliases(commandClass.COMMANDALIASES);
			commandMap.register(commandClass.COMMANDALIASES.get(0), commandClass);
			if (!ignoreStack) stack.add(commandClass);
			MiniGameCore.logger.info("Command "+String.join(", ",commandClass.COMMANDALIASES) + " registered");
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
		commandClass.setAliases(commandClass.COMMANDALIASES);
		getCommandMap().register(commandClass.COMMANDALIASES.get(0), commandClass);
		if (!ignoreStack) stack.add(commandClass);
		MiniGameCore.logger.info("Command "+String.join(", ",commandClass.COMMANDALIASES)+" registered");
	}

	/***
	 * Unregisters an existing registered command class.
	 * @param command The command class to unregister
	 */
	public <T extends MinigameCommand> void unregisterCommand(T command) {
		MinigameCommand commandClass = (MinigameCommand)command;
		getCommandMap().getCommand(commandClass.COMMANDALIASES.get(0)).unregister(getCommandMap());
		if (stack.contains(commandClass)) stack.remove(commandClass);
		MiniGameCore.logger.info("Command "+String.join(", ",commandClass.COMMANDALIASES)+" unregistered");
	}

	/***
	 * Unregisters all registered commands.
	 */
	public void unregisterAllCommands() {
		getCommandMap().clearCommands();
	}

	public void clearStack() {
		stack.forEach(command -> getCommandMap().getCommand(command.COMMANDALIASES.get(0)).unregister(getCommandMap()));
		stack.clear();
	}

	private PluginCommand getCommand(String name) {
		PluginCommand command = null;

		try {
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, MiniGameCore.class);
			c.setAccessible(true);
			command = c.newInstance(name, MiniGameCore.instance);
		} catch (SecurityException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}

		return command;
	}

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
	
	public static CommandManager getInstance(){
		if (instance == null) instance = new CommandManager();
		return instance;
	}
}
