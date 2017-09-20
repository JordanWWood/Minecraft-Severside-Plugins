package network.marble.moderation.punishment.net;

import com.google.common.base.Objects;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.packet.Kick;
import network.marble.moderation.Moderation;

import java.util.Random;
import java.util.UUID;

/**
 * Our own implementation of the BungeeCord DownstreamBridge.<br>
 * Inside here, all packets going from the Minecraft Server to the Minecraft Client are being handled.
 */
public class PunishmentBridge extends DownstreamBridge {
    private final ProxyServer bungee;
    private final UserConnection user;
    private final ServerConnection server;

    private static final Random RANDOM = new Random();

    public PunishmentBridge(ProxyServer bungee, UserConnection user, ServerConnection server) {
        super(bungee, user, server);
        this.bungee = bungee;
        this.user = user;
        this.server = server;
    }

    @Override
    public void exception(Throwable t) throws Exception {
        // Usually, BungeeCord would reconnect the Player to the fallback server or kick him if not
        // Fallback Server is available, when an Exception between the BungeeCord and the Minecraft Server
        // occurs. We override this Method so that we can try to reconnect the client instead.

        if (server.isObsolete()) {
            // do not perform any actions if the user has already moved
            return;
        }
        // setObsolete so that DownstreamBridge.disconnected(ChannelWrapper) won't be called.
        server.setObsolete(true);
    }

    @Override
    public void handle(Kick kick) throws Exception {
        ServerInfo def = bungee.getServerInfo(user.getPendingConnection().getListener().getFallbackServer());
        if (Objects.equal(server.getInfo(), def)) {
            def = null;
        }
        ServerKickEvent event = bungee.getPluginManager().callEvent(new ServerKickEvent(user, server.getInfo(), ComponentSerializer.parse(kick.getMessage()), def, ServerKickEvent.State.CONNECTED));
        if (event.isCancelled() && event.getCancelServer() != null) {
            Moderation.getInstance().getLogger().info("Cancelled");
            user.connectNow(event.getCancelServer());
        } else {
            String kickMessage = ChatColor.stripColor(BaseComponent.toLegacyText(ComponentSerializer.parse(kick.getMessage()))); // cancer
            Moderation.getInstance().getLogger().info(kickMessage);

            boolean doKeepalive = false;
            if (kickMessage.startsWith("KickedForPunishment")) {
                doKeepalive = true;
            }

            Moderation.getInstance().getLogger().info(doKeepalive + "");
            if (!doKeepalive) {
                user.disconnect0(event.getKickReasonComponent());
            } else {
                String regex = "KickedForPunishment-";
                String id = kickMessage.replaceAll(regex, "");
                UUID caseID = UUID.fromString(id);

                Moderation.getInstance().getLogger().info(caseID.toString());

                Moderation.getPunishmentManager().limboIfOnline(user, server, caseID);
            }
        }

        server.setObsolete(true);

        throw CancelSendSignal.INSTANCE;
    }
}