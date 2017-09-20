package network.marble.moderation.commands.staff.discipline;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.Moderation;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.hermes.Hermes;
import network.marble.hermes.PlayerServer;

public class cmdGoTo extends Command {
    public cmdGoTo(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new Thread(() -> {
            User u = null;
            try {
                u = new User().getByUUID(((ProxiedPlayer) sender).getUniqueId());
            } catch (APIException e) {
                e.printStackTrace();
            }

            if (u.hasPermission("moderation.goto")) {
                User target = null;
                try {
                    target = new User().getByUsername(args[0]);
                } catch (APIException e) {
                    e.printStackTrace();
                }

                if (target == null) {
                    ((ProxiedPlayer) sender).sendMessage(ChatColor.RED + "Player " + args[0] + " could not be found. Check capitalisation and spelling");
                    return;
                }

                network.marble.moderation.Moderation.getInstance().getLogger().info(Hermes.networkPlayerMappings.toString());
                network.marble.moderation.Moderation.getInstance().getLogger().info("UUID - " + target.getUuid());
                if (Hermes.networkPlayerMappings.containsKey(target.getUuid())) {
                    final PlayerServer playerServer = Hermes.networkPlayerMappings.get(target.getUuid());
                    network.marble.moderation.Moderation.getInstance().getLogger().info("Player Server - " + playerServer.serverName);

                    ServerInfo info = ProxyServer.getInstance().getServerInfo(playerServer.serverName);
                    network.marble.moderation.Moderation.getInstance().getLogger().info("Info - " + info.getName());
                    if (info != null) {
                        if (!((ProxiedPlayer) sender).getServer().getInfo().equals(info))
                            ((ProxiedPlayer) sender).connect(info);
                        else
                            ((ProxiedPlayer) sender).sendMessage(ChatColor.RED + "This player is already on your server!");
                    }
                } else {
                    network.marble.moderation.Moderation.getInstance().getLogger().info("Player not in network mapping");
                }
            } else {
                ((ProxiedPlayer) sender).sendMessage(ChatColor.RED + "You do not have permission to execute this command");
            }
        }).start();
    }
}
