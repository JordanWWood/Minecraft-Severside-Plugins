package network.marble.minigamecore.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import network.marble.hecate.Hecate;
import network.marble.minigamecore.entities.analytics.server.WorldSetEvent;
import network.marble.minigamecore.entities.analytics.server.WorldUnsetEvent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import lombok.Getter;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.setting.WorldSettings;
import network.marble.minigamecore.entities.world.BlankChunkGenerator;
import network.marble.minigamecore.entities.world.World;
import network.marble.minigamecore.utils.ZipUtils;

public class WorldManager {
    private final List<String> FORBIDDENNAMES = Arrays.asList(".oracle_jre_usage", "logs", "plugins", "timings", "world", "world_nether", "world_the_end");
    @Getter private static HashMap<String, World> currentWorlds;
    @Getter private static HashMap<String, WorldSettings> currentWorldsSettings;

    private static WorldManager instance;
    private static HashMap<String, World> worlds;

    private static final String ZIPFILEPATTERN = ".world.zip";

    private WorldManager() {
        worlds = new HashMap<>();
        currentWorlds = new HashMap<>();
        currentWorldsSettings = new HashMap<>();
        init();
    }

    private void init() {
        MiniGameCore.logger.info("Loading Worlds");

        File loc = null;
        if (!Hecate.devNetFlagSet)
          loc = new File(File.separator + "data" + File.separator + "minigame");
        else loc = new File(MiniGameCore.instance.getDataFolder() + File.separator + "worlds");

        if (!loc.exists()) loc.mkdirs();

        File[] flist = loc.listFiles(file -> file.getPath().toLowerCase().endsWith(ZIPFILEPATTERN));

        if (flist == null) {
            MiniGameCore.logger.warning("Failed to find any world zips out of "+loc.listFiles().length+" files");
            return;
        }

        for (File f : flist) {
            World world = loadWorld(f);
            if (world != null) {
                String worldName = world.getName();
                if (FORBIDDENNAMES.contains(worldName)) continue;
                worlds.put(worldName, world);
                MiniGameCore.logger.info("Loaded World " + worldName);
            }
        }
        MiniGameCore.logger.info("Loaded Worlds");
    }
    
    /***
     * Injects a custom world into the WorldManager
     * @param world The world to inject
     */
    public void injectCustomWorld(org.bukkit.World world) {
    	World w = new World(world.getName(), world.getName(), new ArrayList<>(), null);
    	worlds.put(world.getName(), w);
    }

    public boolean loadWorld(String worldName) {
        MiniGameCore.logger.info("Setting world to "+worldName);
        World world = getWorldByName(worldName);
        MiniGameCore.logger.info("World "+worldName+" found");
        if (world == null) {
            MiniGameCore.logger.info("Unable to find world "+worldName);
            return false;
        }
        AnalyticsManager.getInstance().submitServerEvent(new WorldSetEvent(worldName));
        installWorld(worldName, GameManager.getGameMode());
        MiniGameCore.logger.info("World "+worldName+" installed");
        currentWorlds.put(worldName, world);
        MiniGameCore.logger.info("World "+worldName+" set");
        return true;
    }

    public boolean unloadWorld(String worldName) {
        if (currentWorlds.containsKey(worldName)) {
            AnalyticsManager.getInstance().submitServerEvent(new WorldUnsetEvent(worldName));
            uninstallWorld(worldName);
        }
        return true;
    }

    private void installWorld(String worldName, GameMode mode) {
        if (!currentWorlds.containsKey(worldName)) try {
            World currentWorld = worlds.get(worldName);
            String tempDirectory = MiniGameCore.instance.getDataFolder() + "/temp/";
            ZipUtils.unzip(currentWorld.getZipLocation(), tempDirectory);
            MiniGameCore.logger.info("World zip extracted to temp");

            File tempWorld = new File(tempDirectory + "/world/");
            File worldLocation = new File(new File(".").getAbsolutePath()+"/"+currentWorld.getName());
            if (!worldLocation.exists()) worldLocation.mkdirs();

            FileUtils.copyDirectory(tempWorld, worldLocation);
            MiniGameCore.logger.info("World moved to server");

            FileUtils.deleteDirectory(new File(tempDirectory));
            MiniGameCore.logger.info("World temp cleaned up");

            WorldCreator wc = new WorldCreator(currentWorld.getName());
            wc.generateStructures(false);
            wc.environment(org.bukkit.World.Environment.NORMAL);
            wc.seed(0);
            wc.generator(new BlankChunkGenerator());
            org.bukkit.World world = Bukkit.createWorld(wc);
            world.setGameRuleValue("announceAdvancements", "false");
            MiniGameCore.logger.info("World created in server");

            if (GameManager.getCurrentMiniGame() != null && mode != null && currentWorld.isGameModeSupported(mode)) {
                WorldSettings worldSettings = currentWorld.getGameModeWorldSetting(mode);

                currentWorldsSettings.putIfAbsent(worldName, worldSettings);
                world.setAutoSave(worldSettings.isAutoSave());
                worldSettings.getGameRules().forEach(world::setGameRuleValue);

                world.setAmbientSpawnLimit(worldSettings.getAmbientSpawnLimit());

                world.setSpawnFlags(worldSettings.isMonsterSpawn(), worldSettings.isAnimalSpawn());

                world.setAnimalSpawnLimit(worldSettings.getAnimalSpawnLimit());
                world.setTicksPerAnimalSpawns(worldSettings.getTicksPerAnimalSpawns());

                world.setMonsterSpawnLimit(worldSettings.getMonsterSpawnLimit());
                world.setTicksPerMonsterSpawns(worldSettings.getTicksPerMonsterSpawns());

                world.setWaterAnimalSpawnLimit(worldSettings.getWaterAnimalSpawnLimit());

                world.setTime(worldSettings.getGameTime());
                world.setPVP(worldSettings.isPvp());

                world.setSpawnLocation(worldSettings.getSpawnX(), worldSettings.getSpawnY(), worldSettings.getSpawnZ());

                MiniGameCore.logger.info("World settings loaded");
            }
        } catch(IOException e) {
            MiniGameCore.logger.severe("Failed to install world: "+e.getMessage());
            e.printStackTrace();
        }
        else MiniGameCore.logger.warning("World already loaded");
    }

    private void uninstallWorld(String worldName) {
        if (currentWorlds.containsKey(worldName)) try {
            World currentWorld = currentWorlds.get(worldName);
            org.bukkit.World world = Bukkit.getWorld(currentWorld.getName());
            if (world != null) Bukkit.getServer().unloadWorld(world, false);
            File worldLocation = new File(new File(".").getAbsolutePath()+"/"+currentWorld.getName());
            Runtime.getRuntime().exec("rm -rf " + worldLocation.getAbsolutePath());//TODO security
            /*if (worldLocation.exists()) Files.delete(worldLocation.toPath());*/
        } catch(IOException e) {
            MiniGameCore.logger.info("Failed to uninstall world: "+e.getMessage());
            e.printStackTrace();
        } else MiniGameCore.logger.warning("Failed to find world "+worldName);
    }

    private World loadWorld(File file) {
        String worldInfoPattern = "world.info";
        String gameConfigPattern = ".gamemode.config";
        try {
            ArrayList<String> gameConfigs = new ArrayList<>();
            String worldInfoPath = null;
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry element = entries.nextElement();
                if (element.getName().endsWith(gameConfigPattern)) gameConfigs.add(element.getName());
                if (element.getName().equals(worldInfoPattern)) worldInfoPath = element.getName();
            }
            zipFile.close();
            String name = file.getName().replace(ZIPFILEPATTERN, "");
            return new World(name, file.getAbsolutePath(), gameConfigs, worldInfoPath);

        } catch (Exception e) {
            MiniGameCore.logger.severe("Failed to load world: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public World getWorldByName(String name) {
        return worlds.get(name);
    }

    public String getWorldList() {
        return worlds.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.joining(", ")) + " :"+ worlds.size();
    }

    public List<World> getWorlds() {
        return new ArrayList<World>(worlds.values());
    }

    @SuppressWarnings("serial")
	public HashMap<UUID, List<World>> getWorldsByGameModeId() {
        HashMap<World, List<UUID>> worlds = new HashMap<>();
        HashMap<UUID, List<World>> gamemodes = new HashMap<>();
        getWorlds().forEach(world ->
        {
            worlds.put(world, world.getSupportedGameModeIds());
        });

        worlds.forEach((world, modes) -> modes.forEach(mode -> {
            if (gamemodes.containsKey(mode)) gamemodes.get(mode).add(world);
            else gamemodes.put(mode, new ArrayList<World>(){{add(world);}});
        }));
        return gamemodes;
    }

    public void unloadAllWorlds() {
        currentWorlds.forEach((k,v) -> this.unloadWorld(k));
    }

    public static WorldManager getInstance(boolean forceNew){
        if (instance == null || forceNew) instance = new WorldManager();
        return instance;
    }
}
