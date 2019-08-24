package network.marble.moderationslave.commands;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messagelibrary.api.Lang;
import network.marble.moderationslave.ModerationSlave;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Teleport implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(ModerationSlave.getInstance(), () -> {
            try {
                User u = new User().get(((Player) sender).getUniqueId());
                if (!u.hasPermission("moderation.teleport")) {
                    Lang.chat("general.perm",(Player)sender);
                    return;
                }

                if (args.length == 1) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if (p == null) {
                        Lang.chat("mod.teleport.error.player",(Player)sender);
                        return;
                    }

                    String langPhrase = Lang.get("mod.teleport.success.self",(Player)sender);
                    langPhrase = Lang.replaceUnparsedTag(langPhrase,"player.name",p.getName());
                    sender.sendMessage(langPhrase);
                    Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> ((Player) sender).teleport(p.getLocation()));
                } else if (args.length == 2) {
                    Player p1 = Bukkit.getPlayer(args[0]);
                    Player p2 = Bukkit.getPlayer(args[1]);

                    if (p1 == null || p2 == null) {
                        Lang.chat("mod.teleport.error.player",(Player)sender);
                        return;
                    }

                    String langPhrase = Lang.get("mod.teleport.success.other",(Player)sender);
                    langPhrase = Lang.replaceUnparsedTag(langPhrase,"player1.name",p1.getName());
                    langPhrase = Lang.replaceUnparsedTag(langPhrase,"player2.name",p2.getName());
                    sender.sendMessage(langPhrase);
                    Bukkit.getScheduler().runTask(ModerationSlave.getInstance(), () -> p1.teleport(p2));
                } else {
                    Lang.chat("mod.teleport.error.args",(Player)sender);
                }
            } catch (APIException e) {
                e.printStackTrace();
            }
        });

        return false;
    }
}
