package network.marble.minigamecore;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import network.marble.minigamecore.commands.CoreCommand;
import network.marble.minigamecore.commands.DebugCommand;
import network.marble.minigamecore.commands.HubCommand;
import network.marble.minigamecore.entities.messages.CrashReportMessage;
import network.marble.minigamecore.entities.messages.ServerAvailableMessage;
import network.marble.minigamecore.entities.messages.ServerUnavailableMessage;
import network.marble.minigamecore.listeners.*;
import network.marble.minigamecore.listeners.packet.TabListMenu;
import network.marble.minigamecore.lock.LockFile;
import network.marble.minigamecore.managers.*;
import network.marble.scoreboards.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniGameCore extends JavaPlugin
{
	public static GameManager gameManager;
	public static CommandManager commandManager;
	public static EventManager eventManager;
	public static PlayerManager playerManager;
	public static TeamManager teamManager;
	public static WorldManager worldManager;
	public static PlayerExpectationManager playerExpectationManager;
	public static RabbitManager rabbitManager;
	public static TimerManager timerManager;
	public static PartyManager partyManager;

	public static ProtocolManager protocolManager;

	public static Logger logger;
	public static MiniGameCore instance;
	public static final long TTLMillis = 300000;//5 minutes without players to reboot
	@Getter
	public static UUID instanceId;

	public final static String name = "Mini-Game Core";
	public final static String shortName = "MGC";
	public static final boolean devNetFlagSet = System.getProperty("TESTNET") != null;
	public UUID shutdownTaskId;
	
	public MiniGameCore() {
		logger = getLogger();		
	}

	public void init() {
		instance = this;
		instanceId = UUID.randomUUID();

		if (LockFile.checkLockFile()) {
			CrashReportMessage message = new CrashReportMessage();
			message.serverId = LockFile.getLockFileUUID();
			message.sendToErrors();
			LockFile.deleteLockFile();
		}
		LockFile.createLockFile(instanceId);

		logger.info(name + " initiating");

		Scoreboards.setLogger(logger);

		rabbitManager = RabbitManager.getInstance();
		gameManager = GameManager.getInstance(true);
		worldManager = WorldManager.getInstance(true);
		commandManager = CommandManager.getInstance();
		playerManager = PlayerManager.getInstance();
		teamManager = TeamManager.getInstance();
		playerExpectationManager = PlayerExpectationManager.getInstance();
		eventManager = EventManager.getInstance();
		timerManager = TimerManager.getInstance();
		partyManager = PartyManager.getInstance();

		protocolManager = ProtocolLibrary.getProtocolManager();

		rabbitManager.startQueueConsumer();
		registerCommands();
		registerEvents();

		logger.info(name + " initiated");

		ServerAvailableMessage message = new ServerAvailableMessage();
		message.serverId = instanceId;
		message.sendToCreation();

		stopShutDownTimer();
		startShutDownTimer();
	}

	public void deinit() {
		logger.info(name + " disabling");

		gameManager.unsetCurrentGame();
		worldManager.unloadAllWorlds();
		rabbitManager.stopQueueConsumer();
		timerManager.cleanUp();
		cleanUp();

		LockFile.deleteLockFile();

		instance = null;

		//logger.info("MiniGameCore disabling");
	}

	@Override
	public void onEnable() {
		logger.info(name + " enabled");
		init();
	}

	@Override
	public void onDisable() {
		ServerUnavailableMessage s = new ServerUnavailableMessage();
		s.serverId = instanceId;
		s.sendToServer();
		deinit();
		logger.info(name + " disabled");
	}

	public void startShutDownTimer() {
		shutdownTaskId = TimerManager.getInstance().runIn((timer, last) -> {
			Bukkit.getServer().shutdown();
		}, 5, TimeUnit.MINUTES);
	}

	public void stopShutDownTimer() {
		if (shutdownTaskId != null) TimerManager.getInstance().stopTimer(shutdownTaskId);
	}


	public void registerCommands() {
		if (instance == null) return;
		commandManager.unregisterAllCommands();
		instance.getCommand("debug").setExecutor(new DebugCommand());
		commandManager.registerCommand(new CoreCommand(), true);
		commandManager.registerCommand(new HubCommand(), true);
	}

	public void registerEvents() {
		eventManager.registerEvents(
				true,
				new PlayerEvents(),
				new GameEvents(),
				new MessageEvents(),
				new CommandEvents(),
				new BlockEvents(),
				new EntityEvents(),
				new network.marble.scoreboards.PlayerEvents());

		protocolManager.addPacketListener(new TabListMenu());
	}

	public void cleanUp() {
		ProtocolLibrary.getProtocolManager().removePacketListeners(MiniGameCore.instance);
	}
}
