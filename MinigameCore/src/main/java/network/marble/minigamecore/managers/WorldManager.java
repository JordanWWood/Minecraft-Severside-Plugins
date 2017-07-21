package network.marble.minigamecore.managers;

import lombok.Getter;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.setting.WorldSettings;
import network.marble.minigamecore.entities.world.BlankChunkGenerator;
import network.marble.minigamecore.entities.world.World;
import network.marble.minigamecore.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WorldManager {
    @Getter private static HashMap<String, World> currentWorlds;
    @Getter private static HashMap<String, WorldSettings> currentWorldsSettings;

    private static WorldManager instance;
    private static HashMap<String, World> worlds;

    private static final String ZIPFILEPATTERN = ".world.zip";

    private WorldManager() {
        worlds = new HashMap<>();
        currentWorlds = new HashMap<>();
        currentWorldsSettings = new HashMap<>();
        Init();
    }

    private void Init() {
        MiniGameCore.logger.info("Loading Worlds");
        File loc = MiniGameCore.instance.getDataFolder();

        if (!loc.exists()) loc.mkdirs();

        File[] flist = loc.listFiles(file -> file.getPath().toLowerCase().endsWith(ZIPFILEPATTERN));

        if (flist == null) {
            MiniGameCore.logger.warning("Failed to find any world zips out of "+loc.listFiles().length+" files");
            return;
        }

        for (File f : flist) {
            World world = loadWorld(f);
            if ( world != null) {
                worlds.put(world.getName(), world);
                MiniGameCore.logger.info("Loaded World " + world.getName());
            }
        }
        MiniGameCore.logger.info("Loaded Worlds");
    }

    public boolean loadWorld(String worldName) {
        MiniGameCore.logger.info("Setting world to "+worldName);
        World world = getWorldByName(worldName);
        MiniGameCore.logger.info("World "+worldName+" found");
        if (world == null) {
            MiniGameCore.logger.info("Unable to find world "+worldName);
            return false;
        }
        installWorld(worldName, null);
        MiniGameCore.logger.info("World "+worldName+" installed");
        currentWorlds.put(worldName, world);
        MiniGameCore.logger.info("World "+worldName+" set");
        return true;
    }

    public boolean unloadWorld(String worldName) {
        if (currentWorlds.containsKey(worldName)) {
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
            /*if (worldLocation.exists()) */FileUtils.deleteDirectory(worldLocation);
        } catch(IOException e) {
            MiniGameCore.logger.severe("Failed to uninstall world: "+e.getMessage());
            e.printStackTrace();
        } else MiniGameCore.logger.warning("Failed to find world "+worldName);
    }

    private World loadWorld(File file) {
        String gameConfigPattern = ".gamemode.config";
        try {
            ArrayList<String> gameConfigs = new ArrayList<>();
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry element = entries.nextElement();
                if (element.getName().endsWith(gameConfigPattern)) gameConfigs.add(element.getName()); //.replace(gameConfigPattern, "")
            }
            zipFile.close();
            String name = file.getName().replace(ZIPFILEPATTERN, "");
            return new World(name, file.getAbsolutePath(), gameConfigs);

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
        StringBuilder list = new StringBuilder();
        for(Map.Entry<String, World> entry : worlds.entrySet()) {
            list.append(entry.getKey()+",");
        }
        list.append(" :"+worlds.size());
        return list.toString();
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
            worlds.put(world, world.getSupportGameModeIds());
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
