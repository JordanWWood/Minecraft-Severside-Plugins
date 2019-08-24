package network.marble.minigamecore.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import network.marble.minigamecore.entities.analytics.game.state.ServerStateChangeEvent;
import network.marble.minigamecore.entities.analytics.server.MiniGameSetEvent;
import network.marble.minigamecore.entities.analytics.server.MiniGameUnsetEvent;
import org.bukkit.Bukkit;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.game.GameAbortedEvent;
import network.marble.minigamecore.entities.events.game.GameStatusChangeEvent;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.game.MiniGame;
import network.marble.minigamecore.entities.messages.GameStatusUpdateMessage;

public class GameManager {

	@Getter private static UUID gameId;
	@Getter private static GameMode gameMode;
	@Getter private static MiniGame currentMiniGame;
	@Getter private static GameStatus status;

	private static GameManager instance;
	private static HashMap<UUID,MiniGame> games;

	private GameManager() {
		games = new HashMap<>();
		status = GameStatus.INITIALIZING;
		Init();
	}

	public static void setStatus(GameStatus status) {
		MiniGameCore.logger.info("CHANGING STATE FROM " + GameManager.status + " TO " + status);
		GameStatus oldStatus = GameManager.status;
		GameManager.status = status;
		Bukkit.getServer().getPluginManager().callEvent(new GameStatusChangeEvent(oldStatus, status));
		AnalyticsManager.getInstance().submitAnalyticEvent(new ServerStateChangeEvent(oldStatus, status));
		MiniGameCore.logger.info("STATE CHANGED, MESSAGING ATLAS");
		
    	GameStatusUpdateMessage message = new GameStatusUpdateMessage();
    	message.status = status;
    	message.sendToServer();
	}

	public static void progressStatus() {
		MiniGameCore.logger.info("State progressed");
		setStatus(GameStatus.values()[status.ordinal() == GameStatus.values().length - 1 ? status.ordinal() : status.ordinal() + 1]);
	}

	public static void regressStatus() {
		MiniGameCore.logger.info("State regressed");
		setStatus(GameStatus.values()[status.ordinal() == 0 ? status.ordinal() : status.ordinal() - 1]);
	}

	public boolean setCurrentGame(UUID gameModeId) {
		MiniGameCore.logger.info("Setting mini-game to "+gameModeId.toString());
		GameMode gameMode = new GameMode();
		try {
			gameMode = gameMode.get(gameModeId);
		} catch (APIException e) {
			MiniGameCore.logger.severe("API Failed: "+e.getMessage());
		}
		if (!gameMode.exists()) {
			MiniGameCore.logger.info("Unable to find game mode "+gameModeId.toString());
			return false;
		}
		GameManager.gameId = gameMode.game_id;
		GameManager.gameMode = gameMode;
		AnalyticsManager.getInstance().submitServerEvent(new MiniGameSetEvent(gameMode.getGame_id(), gameMode.getId()));

		MiniGame miniGame = getMiniGameByUUID(gameMode.game_id);
		if (miniGame == null) {
			MiniGameCore.logger.info("Unable to find mini-game for game"+gameMode.game_id.toString());
			return false;
		} else MiniGameCore.logger.info("Mini-game "+miniGame.getName()+" found");

		if(!unsetCurrentGame()) return false;
		GameManager.currentMiniGame = miniGame;

		MiniGameCore.logger.info("Mini-game "+miniGame.getName()+" set");
		
		installMiniGame();
		MiniGameCore.logger.info("DOING POST INSTALL SET TO INIT");
		setStatus(GameStatus.INITIALIZING); //Set for game entry point
		MiniGameCore.logger.info("Mini-game "+miniGame.getName()+" installed");
		return true;
	}

	public boolean unsetCurrentGame() {
		if (currentMiniGame != null){
			AnalyticsManager.getInstance().submitServerEvent(new MiniGameUnsetEvent());
			Bukkit.getServer().getPluginManager().callEvent(new GameAbortedEvent());
			uninstallMiniGame();
			currentMiniGame = null;
		}
		return true;
	}

	private void installMiniGame() {
		MiniGameCore.eventManager.registerEvents(currentMiniGame.getEventListeners());
		MiniGameCore.teamManager.configTeamSetup(currentMiniGame.getTeamSetups());
		MiniGameCore.teamManager.dynamicTeams(currentMiniGame.isNumberOfTeamsDynamic());
	}

	private void uninstallMiniGame() {
		MiniGameCore.eventManager.unregisterEvents(currentMiniGame.getEventListeners());
		MiniGameCore.eventManager.clearStack();
		MiniGameCore.commandManager.clearStack();
		MiniGameCore.timerManager.cleanUp();
		MiniGameCore.instance.cleanUp();
	}

	private void Init() {
		MiniGameCore.logger.info("Loading mini-games");
		File loc = new File(MiniGameCore.instance.getDataFolder(),"games");

		if (!loc.exists()) {
			loc.mkdirs();
			MiniGameCore.logger.warning("Unable to find mini-games folder ("+loc.getAbsolutePath()+")");
			return;
		}

		File[] flist = loc.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));

		if (flist == null) {
			MiniGameCore.logger.warning("Failed to find any mini-game jars out of "+loc.listFiles().length+" files");
			return;
		}

		for (File f : flist) {
			MiniGame mg = loadMiniGame(f, MiniGameCore.instance);
			if(mg != null) {
				try {
					games.put(mg.getGameId(), mg);
					MiniGameCore.logger.info("Loaded mini-game " + mg.getName() +":"+mg.getGameId().toString());
				} catch(AbstractMethodError e){
					MiniGameCore.logger.warning("Mini-game at \"" + f.getName() + "\" is out of date.");
				}
			}
		}
		MiniGameCore.logger.info("Loaded mini-games");
	}

	private MiniGame loadMiniGame(File file, MiniGameCore plugin){
		String infoFileName = "minigame.info";
		try {
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			String mainClass = null;
			while (entries.hasMoreElements()) {
				JarEntry element = entries.nextElement();
				if (element.getName().equalsIgnoreCase(infoFileName)) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
					mainClass = reader.readLine().substring(5);
					reader.close();
					break;
				}
			}
			jarFile.close();
			if (mainClass != null) {
				ClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() }, plugin.getClass()
						.getClassLoader());
				Class<?> classes = Class.forName(mainClass, true, loader);
				for (Class<?> subclasses : classes.getClasses()) {
					Class.forName(subclasses.getName(), true, loader);
				}
				Class<? extends MiniGame> typeClass = classes.asSubclass(MiniGame.class);
                return typeClass.newInstance();
			} else {
				MiniGameCore.logger.warning("Failed to load " + file.getName() + ". Unable to locate "+infoFileName);
			}
		} catch (Exception e) {
			MiniGameCore.logger.severe("Failed to load mini-game: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public MiniGame getMiniGameByUUID(UUID id) {
		return games.get(id);
	}

	public String getMiniGamesList() {
		return games.entrySet().stream().map(e -> e.getValue().getName()).collect(Collectors.joining(", ")) + " :"+ games.size();
	}

	public List<MiniGame> getGames() {
		return new ArrayList<MiniGame>(games.values());
	}
	
	public static GameManager getInstance(boolean forceNew) {
		if (instance == null || forceNew) instance = new GameManager();
		return instance;
	}
}
