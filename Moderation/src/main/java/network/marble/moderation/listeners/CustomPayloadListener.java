package network.marble.moderation.listeners;

import com.google.common.base.Charsets;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import network.marble.moderation.Moderation;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This stops players from sending too many book update packets or sign update packets preventing them from crashing
 * the server.
 *
 * Thanks justblender for this solution to the custom payload exploit.
 *
 * https://github.com/justblender/CustomPayloadFixer/blob/master/src/ru/justblender/bungee/CustomPayloadFixer.java
 */
public class CustomPayloadListener implements Listener {
    private static final Map<Connection, Long> PACKET_USAGE = new ConcurrentHashMap<>();
    private static final Map<Connection, AtomicInteger> CHANNELS_REGISTERED = new ConcurrentHashMap<>();

    private String dispatchCommand = "kick %name%", kickMessage = "CustomPayload kick";

    @EventHandler
    public void onPacket(PluginMessageEvent event) {
        String name = event.getTag();
        if (!"MC|BSign".equals(name) && !"MC|BEdit".equals(name) && !"REGISTER".equals(name))
            return;

        Connection connection = event.getSender();
        if (!(connection instanceof ProxiedPlayer))
            return;

        try {
            if ("REGISTER".equals(name)) {
                if (!CHANNELS_REGISTERED.containsKey(connection))
                    CHANNELS_REGISTERED.put(connection, new AtomicInteger());

                for (int i = 0; i < new String(event.getData(), Charsets.UTF_8).split("\0").length; i++)
                    if (CHANNELS_REGISTERED.get(connection).incrementAndGet() > 124)
                        throw new IOException("Too many channels");
            } else {
                if (elapsed(PACKET_USAGE.getOrDefault(connection, -1L), 100L)) {
                    PACKET_USAGE.put(connection, System.currentTimeMillis());
                } else {
                    throw new IOException("Packet flood");
                }
            }
        } catch (Throwable ex) {
            connection.disconnect(TextComponent.fromLegacyText(kickMessage));

            if (dispatchCommand != null)
                Moderation.getInstance().getProxy().getPluginManager().dispatchCommand(Moderation.getInstance().getProxy().getConsole(),
                        dispatchCommand.replace("%name%", ((ProxiedPlayer) connection).getName()));

            Moderation.getInstance().getLogger().warning(connection.getAddress() + " tried to exploit CustomPayload: " + ex.getMessage());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        CHANNELS_REGISTERED.remove(event.getPlayer());
        PACKET_USAGE.remove(event.getPlayer());
    }

    private boolean elapsed(long from, long required) {
        return from == -1L || System.currentTimeMillis() - from > required;
    }
}
