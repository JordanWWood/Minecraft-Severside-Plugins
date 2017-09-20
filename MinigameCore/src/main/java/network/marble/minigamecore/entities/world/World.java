package network.marble.minigamecore.entities.world;

import com.google.gson.Gson;
import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.setting.WorldSettings;
import network.marble.minigamecore.utils.ZipUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class World {
    @Getter WorldInfo info;
    @Getter String name;
    @Getter String zipLocation;
    @Getter List<WorldSettings> worldSettings;

    public World(String name, String zipLocation, ArrayList<String> gameConfigLocations, String worldInfoLocation){
        this.name = name;
        this.zipLocation = zipLocation;
        this.info = this.loadWorldInfo(worldInfoLocation);
        this.worldSettings = this.loadWorldSetting(gameConfigLocations);
    }

    public List<UUID> getSupportedGameModeIds() {
        return getWorldSettings().stream().map(WorldSettings::getGameModeId).collect(Collectors.toList());
    }

    public List<GameMode> getSupportedGameModes() {
        List<UUID> ids = getSupportedGameModeIds();
        List<GameMode> modes;
        try {
            modes = new GameMode().get(ids);
        } catch(APIException e) {
            MiniGameCore.logger.severe("API failed: "+e.getMessage());
            modes = new ArrayList<>();
        }
        return modes;
    }

    public boolean isGameModeSupported(GameMode mode) {
        if (mode == null) return false;
        long count = getWorldSettings().stream().filter(worldSetting -> worldSetting.getGameModeId().equals(mode.getId())).count();
        return count > 0;
    }

    public WorldSettings getGameModeWorldSetting(GameMode mode) {
        return getWorldSettings().stream().filter(worldSetting -> worldSetting.getGameModeId().equals(mode.getId())).findFirst().orElse(null);
    }

    private WorldInfo loadWorldInfo(String worldInfoLocation) {
        Gson g = new Gson();
        String file = loadFile(worldInfoLocation);
        if (file != null) return g.fromJson(file, WorldInfo.class);
        else return new WorldInfo();
    }

    private List<WorldSettings> loadWorldSetting(List<String> gameConfigLocations) {
        List<WorldSettings> settings = new ArrayList<>();
        Gson g = new Gson();
        gameConfigLocations.forEach(location -> {
            String file = loadFile(location);
            if (file != null) settings.add(g.fromJson(file, WorldSettings.class));
        });
        return settings;
    }

    public String loadFile(String location) {
        try {
            return ZipUtils.unzipFile(zipLocation, location);
        } catch (IOException e) {
            MiniGameCore.logger.severe("Failed to load file: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
