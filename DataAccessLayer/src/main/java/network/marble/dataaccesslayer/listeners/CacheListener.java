package network.marble.dataaccesslayer.listeners;

import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.managers.CacheRabbitManager;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import java.io.IOException;
import java.util.UUID;

public class CacheListener implements CacheEventListener<UUID, String> {
    @Override
    public void onEvent(CacheEvent<? extends UUID, ? extends String> event) {
        try {
            CacheRabbitManager.getInstance().unbindCacheKey(event.getKey());
        } catch (IOException e) {
            DataAccessLayer.instance.logger.severe("Failed to unbind key '"+event.getKey()+"' due to: "+e.getMessage());
            e.printStackTrace();
        }

    }
}
