package network.marble.dataaccesslayer.managers;

import com.github.jedis.lock.JedisLock;
import lombok.Setter;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.common.Context;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

/**
 * Provides access to the Redis layer of the Marble.
 */
public class RedisManager {
    /**
     * Connection pool
     */
    private static JedisPool pool;
    @Setter
    private static boolean shutdown = false;

    /**
     * Returns the JedisPool or creates it if one does not exist
     */
    public static JedisPool getPool(){
        if(pool == null || pool.isClosed() && !shutdown) {
            pool = new JedisPool(new JedisPoolConfig(), DataAccessLayer.getEnvironmentalVariable("REDIS_NODE_ADDRESS"));
        }

        return pool;
    }

    /**
     * Releases the given JedisLock object. Null locks are ignored.
     */
    public static void releaseLock(JedisLock lock) {
        if(lock != null) lock.release();
    }

    /**
     * Gets a UUID object from the given Jedis key
     * @param jedis The Jedis object to get the UUID through
     * @param key The key to convert the value of to UUID
     * @return A UUID if successful or null if for any reason unsuccessful
     */
    public static UUID getUUID(Jedis jedis, String key){
        String raw = jedis.get(key);

        if(raw != null) try{
            return UUID.fromString(raw);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return null;
    }
}
