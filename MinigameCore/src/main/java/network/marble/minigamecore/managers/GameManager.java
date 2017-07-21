package network.marble.minigamecore.managers;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.game.MiniGame;
import network.marble.minigamecore.entities.task.CoreTickTask;
import network.marble.minigamecore.entities.events.game.GameAbortedEvent;
import network.marble.minigamecore.entities.events.game.GameStatusChangeEvent;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class GameManager {
	private static BukkitTask coreTickTask;

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
		coreTickTask = new CoreTickTask().runTaskTimerAsynchronously(MiniGameCore.instance, 20, 20);
	}

	public static void setStatus(GameStatus status) {
		Bukkit.getServer().getPluginManager().callEvent(new GameStatusChangeEvent(getStatus(), status));
		GameManager.status = status;
	}

	public static void progressStatus() {
		setStatus(GameStatus.values()[status.ordinal() == GameStatus.values().length - 1 ? status.ordinal() : status.ordinal() + 1]);
	}

	public static void regressStatus() {
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
		this.gameId = gameMode.game_id;
		this.gameMode = gameMode;
		MiniGame miniGame = getMiniGameByUUID(gameMode.game_id);
		if (miniGame == null) {
			MiniGameCore.logger.info("Unable to find mini-game for game"+gameMode.game_id.toString());
			return false;
		} else MiniGameCore.logger.info("Mini-game "+miniGame.getName()+" found");

		if(!unsetCurrentGame()) return false;
		this.currentMiniGame = miniGame;

		setStatus(GameStatus.INITIALIZING);
		MiniGameCore.logger.info("Mini-game "+miniGame.getName()+" set");
		installMiniGame();
		setStatus(GameStatus.INITIALIZING); //Secondary set for game entry point
		MiniGameCore.logger.info("Mini-game "+miniGame.getName()+" installed");
		return true;
	}

	public boolean unsetCurrentGame() {
		if (currentMiniGame != null){
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
		File loc = MiniGameCore.instance.getDataFolder();

		if (!loc.exists()) {
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
		String list = "";
		for(Map.Entry<UUID, MiniGame> entry : games.entrySet()) {
			list +=entry.getValue().getName()+",";
		}
		list += " :"+games.size();
		return list;
	}

	public List<MiniGame> getGames() {
		return new ArrayList<MiniGame>(games.values());
	}
	
	public static GameManager getInstance(boolean forceNew) {
		if (instance == null || forceNew) instance = new GameManager();
		return instance;
	}
}
