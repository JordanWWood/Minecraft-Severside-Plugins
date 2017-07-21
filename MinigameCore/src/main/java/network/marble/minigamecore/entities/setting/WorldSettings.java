package network.marble.minigamecore.entities.setting;

import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class WorldSettings {
    @Getter UUID gameModeId;
    @Getter boolean autoSave = false;
    @Getter HashMap<String, String> gameRules;

    @Getter boolean disableLeafDecay = false;
    @Getter boolean disableBlockBreaking = false;
    @Getter boolean disableBlockPlacing = false;

    @Getter int ambientSpawnLimit;

    @Getter boolean animalSpawn;
    @Getter int animalSpawnLimit;
    @Getter int ticksPerAnimalSpawns;

    @Getter boolean monsterSpawn;
    @Getter int monsterSpawnLimit;
    @Getter int ticksPerMonsterSpawns;

    @Getter int waterAnimalSpawnLimit;

    @Getter long gameTime = 0;
    @Getter boolean pvp = true;

    @Getter int spawnX = 0;
    @Getter int spawnY = 0;
    @Getter int spawnZ = 0;
}
