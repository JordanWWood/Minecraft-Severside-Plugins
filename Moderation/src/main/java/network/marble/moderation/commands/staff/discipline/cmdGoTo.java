package network.marble.moderation.commands.staff.discipline;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.hermes.Hermes;
import network.marble.hermes.PlayerServer;
import network.marble.moderation.Moderation;

public class cmdGoTo extends Command {
    public cmdGoTo(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(Moderation.getInstance(), () -> {
            User u = null;
            try {
                u = new User().get(((ProxiedPlayer) sender).getUniqueId());
            } catch (APIException e) {
                ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "An error occurred whilst checking your permission. Assuming no permissions"));
                return;
            }

            if (u.hasPermission("moderation.goto")) {
                User target = null;
                try {
                    target = new User().getByUsername(args[0]);
                } catch (APIException e) {
                    ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "An error occurred whilst attempting to find the player"));
                }

                if (target == null) {
                    ((ProxiedPlayer) sender).sendMessage(ChatColor.RED + "Player " + args[0] + " could not be found. Check capitalisation and spelling");
                    return;
                }

                if (Hermes.networkPlayerMappings.containsKey(target.getUuid())) {
                    final PlayerServer playerServer = Hermes.networkPlayerMappings.get(target.getUuid());

                    ServerInfo info = ProxyServer.getInstance().getServerInfo(playerServer.serverName);
                    if (info != null) {
                        if (!((ProxiedPlayer) sender).getServer().getInfo().equals(info))
                            ((ProxiedPlayer) sender).connect(info);
                        else
                            ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "This player is already on your server!"));
                    }
                } else {
                    ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "Player is not online or not mapped to a server!"));
                }
            } else {
                ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to execute this command"));
            }
        });
    }
}
