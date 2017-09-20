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

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.hermes.Hermes;
import network.marble.moderation.Moderation;
import network.marble.moderation.utils.ScheduleAsync;

public class PunishmentTask {
    private static final Random RANDOM = new Random();
    private static final TextComponent EMPTY = new TextComponent("");

    private final ProxyServer bungee;
    private final UserConnection user;
    private final ServerConnection server;
    private final BungeeServerInfo target;
    public Case aCase;

    @Getter List<String> messages = new ArrayList<>();
    @Getter @Setter boolean release = false;

    PunishmentTask(ProxyServer bungee, UserConnection user, ServerConnection server, Case aCase) {
        this.bungee = bungee;
        this.user = user;
        this.server = server;
        this.target = server.getInfo();
        this.aCase = aCase;
    }

    int i = 0;
    void tick() {
        // Send KeepAlive Packet so that the client won't time out.
        user.unsafe().sendPacket(new KeepAlive(RANDOM.nextInt()));

        for (String s : messages) {
            user.sendMessage(s);
        }
        messages.clear();

        User u = null;
        Case c = null;
        try {
            u = new User().getByUUID(user.getUniqueId());
            c = new Case().get(aCase.getId());
        } catch (APIException e) {
            e.printStackTrace();
        }

        if(c != null) {
            Moderation.getInstance().getLogger().info(c.isPardoned() + " pardoned");
            Moderation.getInstance().getLogger().info(c.getId() + "");
            if (c.isPardoned()) {
                //move player to sufficient hub
                Moderation.getPunishmentManager().cancelPunishmentTask(user.getUniqueId());
                user.connect(Hermes.getBestHub(server.getInfo().getName()).getServerInfo());

                return;
            } else if (c.getOutcome().equals(CaseOutcome.PermanentlyBan)) {
                user.disconnect("You have been permanently banned!");
            } else if (c.getOutcome().equals(CaseOutcome.TemporaryBan)) {
                user.disconnect("You have been banned!");
            }
        }

        // Schedule next tick.
        new ScheduleAsync(() -> {
                if (Moderation.getPunishmentManager().isUserOnline(user) && Objects.equals(user.getServer(), server)) {
                    tick();
                } else {
                    Moderation.getPunishmentManager().cancelPunishmentTask(user.getUniqueId());
                }
            } , Moderation.getPunishmentManager().getKeepAliveMillis(), TimeUnit.MILLISECONDS);
    }
}
