package network.marble.moderation.punishment;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import network.marble.moderation.Moderation;
import network.marble.moderation.utils.SheduleAsync;

public class PunishmentTask {
    private static final Random RANDOM = new Random();
    private static final TextComponent EMPTY = new TextComponent("");

    private final ProxyServer bungee;
    private final UserConnection user;
    private final ServerConnection server;
    private final BungeeServerInfo target;

    @Getter List<String> messages = new ArrayList<>();
    @Getter @Setter boolean release = false;

    PunishmentTask(ProxyServer bungee, UserConnection user, ServerConnection server) {
        this.bungee = bungee;
        this.user = user;
        this.server = server;
        this.target = server.getInfo();
    }

    void tick() {
        // Send KeepAlive Packet so that the client won't time out.
        user.unsafe().sendPacket(new KeepAlive(RANDOM.nextInt()));

        user.sendMessage("Limbo");
        for (String s : messages) {
            user.sendMessage(s);
        }
        messages.clear();

        // TODO check if they are no longer under punishment and if so change release to true
        // TODO check if they have been banned or kicked from the network

        if (release) {
            // TODO move player to sufficient hub
            Moderation.getPunishmentManager().cancelPunishmentTask(user.getUniqueId());
        }

        // Schedule next tick.
        new SheduleAsync(new Runnable() {
            @Override
            public void run() {
                if (Moderation.getPunishmentManager().isUserOnline(user) && Objects.equals(user.getServer(), server)) {
                    tick();
                } else {
                    Moderation.getPunishmentManager().cancelPunishmentTask(user.getUniqueId());
                }
            }
        }, Moderation.getPunishmentManager().getKeepAliveMillis(), TimeUnit.MILLISECONDS);
    }
}
