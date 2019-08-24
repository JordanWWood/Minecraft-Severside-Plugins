package network.marble.minigamecore.entities.setting;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;

import lombok.Getter;

public class WorldSettings {
    @Getter UUID gameModeId;
    @Getter boolean autoSave = false;
    @Getter HashMap<String, String> gameRules = new HashMap<>();

    @Getter boolean disableLeafDecay = true;
    @Getter boolean disableFireSpread = true;

    @Getter boolean disableBlockIgnite = true;
    @Getter boolean disableBlockSpread = true;
    @Getter boolean disableBlockFade = true;
    @Getter boolean disableBlockBreaking = true;
    @Getter boolean disableBlockPlacing = true;
    @Getter boolean disableBlockExplosionDamage = true;

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
    
    @Getter Material icon;
}
