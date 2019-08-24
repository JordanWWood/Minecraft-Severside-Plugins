package network.marble.dataaccesslayer.managers;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import network.marble.dataaccesslayer.base.DataAccessLayer;

import network.marble.dataaccesslayer.listeners.CacheListener;
import network.marble.dataaccesslayer.models.base.BaseModel;
import org.ehcache.Cache;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.EventType;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CacheManager {
    public final static boolean enabled = true;

    private static CacheManager instance;
    private org.ehcache.CacheManager cacheManager;
    @Getter private CacheRabbitManager rabbitManager;

    private CacheManager() {
        CacheConfiguration<UUID, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, String.class,
            ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(1, MemoryUnit.MB)
                .offheap(10, MemoryUnit.MB)
                .disk(50, MemoryUnit.MB)
        )
            .withExpiry(Expirations.timeToLiveExpiration(Duration.of(5, TimeUnit.MINUTES)))
            .add(CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(new CacheListener(), EventType.EXPIRED, EventType.EVICTED))
            .build();
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(new File(DataAccessLayer.instance.getDataFolder(), "cacheData").getAbsolutePath()))
                .withCache("main", cacheConfiguration).build(true);

        cleanUp();

        if (DataAccessLayer.instance != null) {
            rabbitManager = CacheRabbitManager.getInstance();
            rabbitManager.startQueueConsumer();
        }
    }

    protected Cache<UUID, String> getMainCache() {
        return cacheManager.getCache("main", UUID.class, String.class);
    }

    public void cleanUp() {
        getMainCache().clear();
    }

    public boolean containsKey(UUID uuid) {
        return getMainCache().containsKey(uuid);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseModel> T get(UUID uuid, Class<?> clazz) {
        return deserializeModel(getMainCache().get(uuid), clazz);

    }

    @SuppressWarnings("unchecked")
    public <T extends BaseModel> List<T> getAll(List<UUID> uuids, Class<?> clazz) {
        return getMainCache().getAll(new HashSet<>(uuids)).values()
                .stream().map(obj -> (T)deserializeModel(obj, clazz)).collect(Collectors.toList());
    }

    public <T extends BaseModel> void put(UUID uuid, T item) {
        if (!item.exists()) return;


        getMainCache().put(uuid, item.toJSON());

        try {
            CacheRabbitManager.getInstance().bindCacheKey(uuid);
        } catch (IOException e) {
            DataAccessLayer.instance.logger.severe("Failed to bind key '"+uuid+"' due to: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public <T extends BaseModel> void replace(UUID uuid, T item) {
        if (!item.exists()) return;
        getMainCache().replace(uuid, item.toJSON());
    }

    public void remove(UUID uuid) {
        getMainCache().remove(uuid);
    }

    public static CacheManager getInstance() {
        if (instance == null) instance = new CacheManager();
        return instance;
    }

    protected <E> E deserializeModel(String data, Class<?> clazz) {
        if (data == null || data.equals("null")) data = "{}";
        JavaType type = new ObjectMapper().getTypeFactory().constructType(clazz);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
