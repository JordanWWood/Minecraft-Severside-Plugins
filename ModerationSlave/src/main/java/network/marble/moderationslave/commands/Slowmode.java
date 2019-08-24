package network.marble.moderationslave.commands;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messagelibrary.api.Lang;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Slowmode implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(ModerationSlave.getInstance(), () -> {
            User u = null;
            try {
                u = new User().getByUUID(((Player) sender).getUniqueId());

                if (u.hasPermission("moderation.slowmode")) {
                    PlayerListener.setSlowmode(true);

                    if (args.length >= 1) {
                        if (args[0].equalsIgnoreCase("off")) PlayerListener.setSlowmode(false);
                        else PlayerListener.setSlowmodeSpeed(Integer.valueOf(args[0]));

                        String phrase = Lang.get("mod.slowmode.success",(Player)sender);
                        phrase = Lang.replaceUnparsedTag(phrase,"speed.amount",args[0]);
                        sender.sendMessage(phrase);
                    }
                } else {
                    Lang.chat("general.perm",(Player)sender);
                }
            } catch (APIException e) {
                e.printStackTrace();
            }
        });

        return false;
    }
}
