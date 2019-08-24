package network.marble.minigamecore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import network.marble.hecate.Hecate;
import network.marble.minigamecore.commands.CoreCommand;
import network.marble.minigamecore.commands.DebugCommand;
import network.marble.minigamecore.commands.ElevateCommand;
import network.marble.minigamecore.commands.HubCommand;
import network.marble.minigamecore.commands.TPCommand;
import network.marble.minigamecore.entities.messages.CrashReportMessage;
import network.marble.minigamecore.entities.messages.ServerAvailableMessage;
import network.marble.minigamecore.entities.messages.ServerUnavailableMessage;
import network.marble.minigamecore.listeners.BlockEvents;
import network.marble.minigamecore.listeners.CommandEvents;
import network.marble.minigamecore.listeners.EntityEvents;
import network.marble.minigamecore.listeners.GameEvents;
import network.marble.minigamecore.listeners.MessageEvents;
import network.marble.minigamecore.listeners.PlayerEvents;
import network.marble.minigamecore.listeners.packet.TabListMenu;
import network.marble.minigamecore.lock.LockFile;
import network.marble.minigamecore.managers.CommandManager;
import network.marble.minigamecore.managers.EventManager;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PartyManager;
import network.marble.minigamecore.managers.PlayerExpectationManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.RabbitManager;
import network.marble.minigamecore.managers.TeamManager;
import network.marble.minigamecore.managers.TimerManager;
import network.marble.minigamecore.managers.WorldManager;
import network.marble.scoreboards.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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

    public final static String name = "MiniGame Core";
    public final static String shortName = "MGC";
    public UUID shutdownTaskId;

    public MiniGameCore() {
        logger = getLogger();
    }

    public void init() {
        instance = this;
        try {
            String[] parts = Hecate.getServerName().split("_");
            if (parts.length <= 1 || Hecate.devNetFlagSet)
                instanceId = UUID.randomUUID();
            else
                instanceId = UUID.fromString(parts[1]);
        } catch (Exception e) {
            logger.warning("Failed to get UUID from Hecate due to: "+e.getMessage());
            instanceId = UUID.randomUUID();
        }

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

        //Sigterm shutdown for containerised environments
        Runtime.getRuntime().addShutdownHook(new Thread(() -> getServer().shutdown()));
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
        MiniGameCore.instance.getLogger().info("No players online! Shutting down in 3 minutes.");
        shutdownTaskId = TimerManager.getInstance().runIn((timer, last) -> {
            Bukkit.getServer().shutdown();
            MiniGameCore.instance.getLogger().info("No players for 3 minutes, shutting down for updates.");
        }, 3, TimeUnit.MINUTES);
    }

    public void stopShutDownTimer() {
        if (shutdownTaskId != null) {
            MiniGameCore.instance.getLogger().info("Player joined during shutdown timer! Cancelling timer.");
            TimerManager.getInstance().stopTimer(shutdownTaskId);
            shutdownTaskId = null;
        }
    }


    public void registerCommands() {
        if (instance == null) return;
        commandManager.unregisterAllCommands();
        commandManager.registerCommand(new DebugCommand(), true);
        commandManager.registerCommand(new CoreCommand(), true);
        commandManager.registerCommand(new HubCommand(), true);
        commandManager.registerCommand(new TPCommand(), true);
        commandManager.registerCommand(new ElevateCommand(), true);
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
