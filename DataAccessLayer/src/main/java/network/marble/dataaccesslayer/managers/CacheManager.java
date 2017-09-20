package network.marble.dataaccesslayer.managers;

import lombok.Getter;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.util.UUID;

public class CacheManager {
    public final static boolean enabled = true;
    private static CacheManager instance;
    private org.ehcache.CacheManager cacheManager;
    @Getter private Cache<UUID, Object> cache;
    @Getter private RabbitManager rabbitManager;

    private CacheManager() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        cache = cacheManager.createCache("main", CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, Object.class, ResourcePoolsBuilder.heap(100)).build());

        rabbitManager = new RabbitManager();
        rabbitManager.startQueueConsumer();
    }

    public void cleanUp() {
        cacheManager.close();
    }

    public static CacheManager getInstance() {
        if (instance == null) instance = new CacheManager();
        return instance;
    }
}
