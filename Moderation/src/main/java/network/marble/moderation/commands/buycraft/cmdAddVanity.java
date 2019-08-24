package network.marble.moderation.commands.buycraft;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderation.utils.ScheduleAsync;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class cmdAddVanity extends Command {
    public cmdAddVanity(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("buy.add.vanity")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to run buycraft commands!"));
            return;
        }

        new ScheduleAsync(() -> {
            try {
                User u = new User().getByUsername(args[0]);

                if (!u.exists()) {
                    //TODO handle
                } else {
                    VanityItem item = new VanityItem().get(UUID.fromString(args[1]));
                    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u.getUuid());

                    if (item == null || !item.exists()) {
                        if(UUID.fromString(args[1]).equals(UUID.fromString("4b6a2a2c-486c-4d53-9ee6-4aa5e65c08f4"))) {
                            if (u.getVanityitems().containsKey(UUID.fromString(args[1])))
                                u.getVanityitems().replace(UUID.fromString(args[1]), u.getVanityitems().get(UUID.fromString(args[1])) + Integer.parseInt(args[2]));
                            else
                                u.getVanityitems().put(UUID.fromString(args[1]), Integer.valueOf(args[2]));

                            u.save();

                            p.sendMessage(new TextComponent(ChatColor.GREEN + "Due to your recent purchase you have been given " + Integer.valueOf(args[2]) + " TicketBomb(s)!"));
                            return;
                        } else {
                            p.sendMessage(new TextComponent(ChatColor.RED + "Due to an error we failed to award one of your purchased items! \n" +
                                    ChatColor.RED + "Please create a bug report showing an image of the following information:\n" +
                                    ChatColor.RED + "Item ID: " + args[1] + "\n" +
                                    ChatColor.RED + "UUID: " + args[0]) + "\n" +
                                    ChatColor.RED + "Amount: " + args[2]);

                            return;
                        }
                    }

                    if (u.getVanityitems().containsKey(UUID.fromString(args[1])))
                        u.getVanityitems().replace(UUID.fromString(args[1]), u.getVanityitems().get(UUID.fromString(args[1])) + Integer.parseInt(args[2]));
                    else
                        u.getVanityitems().put(UUID.fromString(args[1]), Integer.valueOf(args[2]));
                    
                    u.save();

                    p.sendMessage(new TextComponent(ChatColor.GREEN + "Due to your recent purchase you have unlocked " + item.getName()));
                }
            } catch (APIException e) {
                e.printStackTrace();
            }
        }, 15L, TimeUnit.SECONDS);
    }
}
