package network.marble.minigamecore.entities.world;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.World;
import java.util.Random;

public class BlankChunkGenerator extends ChunkGenerator {
    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid)
    {
        return new byte[world.getMaxHeight() / 16][];
    }
}
