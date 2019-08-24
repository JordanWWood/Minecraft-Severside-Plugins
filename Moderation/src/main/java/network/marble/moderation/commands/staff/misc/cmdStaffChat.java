package network.marble.moderation.commands.staff.misc;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderation.Moderation;
import network.marble.moderation.messages.ChatMessage;

public class cmdStaffChat extends Command {
    public cmdStaffChat(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        ProxyServer.getInstance().getScheduler().runAsync(Moderation.getInstance(), () -> {
            try {
                User u = new User().get(((ProxiedPlayer) sender).getUniqueId());

                if (u.hasPermission("mod.staff.chat")) {
                    StringBuilder line = new StringBuilder();
                    for (String s : strings) line.append(" ").append(s);

                    ChatMessage message = new ChatMessage();
                    message.setUuid(((ProxiedPlayer) sender).getUniqueId());
                    message.setMessage(line.toString());

                    message.sendMessage(149, "staff.all");
                } else {
                    ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to execute this command"));
                }
            } catch (APIException e) {
                ((ProxiedPlayer) sender).sendMessage(new TextComponent(ChatColor.RED + "We failed to check your permissions! Assuming no permission"));
            }
        });
    }
}
