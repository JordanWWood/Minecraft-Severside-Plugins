package network.marble.moderationslave.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import network.marble.moderationslave.ModerationSlave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomPayloadManager {
    private static final Map<Player, Long> PACKET_USAGE = new ConcurrentHashMap<>();

    public CustomPayloadManager() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ModerationSlave.getInstance(), PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                checkPacket(event);
            }
        });

        Bukkit.getScheduler().runTaskTimer(ModerationSlave.getInstance(), () -> {
            for (Iterator<Map.Entry<Player, Long>> iterator = PACKET_USAGE.entrySet().iterator(); iterator.hasNext(); ) {
                Player player = iterator.next().getKey();
                if (!player.isOnline() || !player.isValid())
                    iterator.remove();
            }
        }, 20L, 20L);
    }

    private void checkPacket(PacketEvent event) {
        Player player = event.getPlayer();
        long lastPacket = PACKET_USAGE.getOrDefault(player, -1L);

        if (lastPacket == -2L) {
            event.setCancelled(true);
            return;
        }

        String name = event.getPacket().getStrings().readSafely(0);
        if (!"MC|BSign".equals(name) && !"MC|BEdit".equals(name) && !"REGISTER".equals(name))
            return;

        try {
            if ("REGISTER".equals(name)) {
                checkChannels(event);
            } else {
                if (elapsed(lastPacket, 100L)) {
                    PACKET_USAGE.put(player, System.currentTimeMillis());
                } else {
                    throw new IOException("Packet flood");
                }

                checkNbtTags(event);
            }
        } catch (Throwable ex) {
            // Set last packet usage to -2 so we wouldn't mind checking him again
            PACKET_USAGE.put(player, -2L);

            Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> player.kickPlayer("Custom Payload Exploit Detected"));

            ModerationSlave.getInstance().getLogger().info(player.getName() + " tried to exploit CustomPayload: " + ex.getMessage());
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    private void checkNbtTags(PacketEvent event) throws IOException {
        PacketContainer container = event.getPacket();
        ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();

        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);

        try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes))) {
            ItemStack itemStack = StreamSerializer.getDefault().deserializeItemStack(inputStream);
            if (itemStack == null)
                throw new IOException("Unable to deserialize ItemStack");

            NbtCompound root = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            if (root == null) {
                throw new IOException("No NBT tag?!");
            } else if (!root.containsKey("pages")) {
                throw new IOException("No 'pages' NBT compound was found");
            } else {
                NbtList<String> pages = root.getList("pages");
                if (pages.size() > 50)
                    throw new IOException("Too many pages");

                /*
                //Minecraft clients allow players to use § symbols in books.
                for (String page : pages)
                    if (COLOR_PATTERN.matcher(page).replaceAll("").length() > 257)
                        throw new IOException("A very long page");
                */
            }
        } finally {
            buffer.release();
        }
    }

    private void checkChannels(PacketEvent event) throws Exception {
        int channelsSize = event.getPlayer().getListeningPluginChannels().size();

        PacketContainer container = event.getPacket();
        ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();

        try {
            for (int i = 0; i < buffer.toString(Charsets.UTF_8).split("\0").length; i++)
                if (++channelsSize > 124)
                    throw new IOException("Too much channels");
        } finally {
            buffer.release();
        }
    }

    private boolean elapsed(long from, long required) {
        return from == -1L || System.currentTimeMillis() - from > required;
    }
}
