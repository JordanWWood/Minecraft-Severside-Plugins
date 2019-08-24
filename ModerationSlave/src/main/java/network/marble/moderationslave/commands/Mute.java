package network.marble.moderationslave.commands;

import static network.marble.moderationslave.utils.Time.DAY;
import static network.marble.moderationslave.utils.Time.HOUR;
import static network.marble.moderationslave.utils.Time.MINUTE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.marble.messagelibrary.api.Lang;
import network.marble.moderationslave.ModerationSlave;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderationslave.utils.FontFormat;

public class Mute implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(ModerationSlave.getInstance(), () -> {
            User u = null;
            try {
                u = new User().getByUUID(((Player) sender).getUniqueId());
            } catch (APIException e) {
                sender.sendMessage("An error occurred whilst trying to execute that command. Take note of the time and let a developer know");
                e.printStackTrace();
                return;
            }

            if (u.hasPermission("moderation.mute")) {
                if (args.length < 1) {
                    Lang.chat("mod.staff.mute.args",(Player)sender);
                    return;
                }

                if (!args[0].equalsIgnoreCase("ip") && !args[0].equalsIgnoreCase("none")) {
                    Lang.chat("mod.staff.mute.args",(Player)sender);
                    return;
                }

                long totalMillis = 0L;
                int length = 0;
                boolean ip = args[0].equalsIgnoreCase("ip");

                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    Lang.chat("mod.staff.mute.player.notfound",(Player)sender);
                    return;
                }

                User target = null;
                try {
                    target = new User().get(p.getUniqueId());
                } catch (APIException e) {
                    sender.sendMessage(FontFormat.translateString("&cAn error occurred whilst retrieving the user " + args[1]));
                    return;
                }

                if (!u.exists()) {
                    sender.sendMessage(FontFormat.translateString("&cUser " + args[1] + "could not be found"));
                    return;
                }

                for (int i = 2; i < args.length; i++) {
                    if (args[2].equalsIgnoreCase("permanent")) {
                        totalMillis = Long.MIN_VALUE;
                        break;
                    }

                    String time = args[i].replaceAll("[^A-Za-z]+", "");
                    if (time.equalsIgnoreCase("d")) {
                        totalMillis += DAY.getMillis() * getPeriod(args[i]);
                    } else if (time.equalsIgnoreCase("h")) {
                        totalMillis += HOUR.getMillis() * getPeriod(args[i]);
                    } else if (time.equalsIgnoreCase("m")) {
                        totalMillis += MINUTE.getMillis() * getPeriod(args[i]);
                    } else {
                        length = i;
                        break;
                    }
                }

                if (totalMillis == 0) {
                    Lang.chat("mod.staff.mute.duration",(Player)sender);
                    return;
                }

                StringBuilder b = new StringBuilder();
                for (int i = length; i < args.length; i++) {
                    b.append(args[i] + " ");
                }
                String reason = b.toString();
                ModerationSlave.getInstance().getLogger().info(reason);


                Case c = new Case();
                c.setDescription(reason);
                c.setClosed_at(System.currentTimeMillis());

                if (ip) c.setOutcome(CaseOutcome.IpMuted);
                else c.setOutcome(CaseOutcome.Muted);

                c.setPardoned(false);
                c.setOutcome_duration(totalMillis + System.currentTimeMillis());
                c.setJudgementee_id(target.getUuid());
                c.setAssignee_id(u.getUuid());
                c.setCreated_by(u.getUuid());
                c.setCreated_at(System.currentTimeMillis());

                try {
                    c.save();
                } catch (APIException e) {
                    e.printStackTrace();
                }

                Lang.chat("mod.staff.mute.success",(Player)sender);
            }
        });
        return false;
    }

    public long getPeriod(String s) {
        String number = "";

        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(s);
        while (m.find()) {
            number += m.group(1);
        }

        return Long.parseLong(number);
    }
}
