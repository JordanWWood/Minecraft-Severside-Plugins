package network.marble.moderationslave.commands;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderationslave.utils.FontFormat;
import network.marble.moderationslave.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static network.marble.moderationslave.utils.Time.*;
import static network.marble.moderationslave.utils.Time.DAY;
import static network.marble.moderationslave.utils.Time.HOUR;

public class cmdMute implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        new Thread(() -> {
            User u = null;
            try {
                u = new User().getByUUID(((Player) sender).getUniqueId());
            } catch (APIException e) {
                e.printStackTrace();
            }

            if (u.hasPermission("moderation.mute")) {
                Long totalMillis = 0L;
                int length = 0;

                Player p = Bukkit.getPlayer(args[0]);
                User target = null;
                try {
                    u = new User().get(p.getUniqueId());
                } catch (APIException e) {
                    e.printStackTrace();
                }

                if (u == null) {
                    sender.sendMessage(FontFormat.translateString("&cUser " + args[0] + "could not be found"));
                    return;
                }

                for (int i = 1; i < args.length; i++) {
                    if (args[1].equalsIgnoreCase("permanant")) {
                        totalMillis = 157784630000L;
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

                StringBuilder b = new StringBuilder();
                for (int i = length; i < args.length; i++) {
                    b.append(args[i]);
                }

                Case c = new Case();
                c.setDescription(b.toString());
                c.setClosed_at(System.currentTimeMillis());
                c.setOutcome(CaseOutcome.Muted);
                c.setPardoned(false);
                c.setOutcome_duration(totalMillis + System.currentTimeMillis());
                c.setJudgementee_id(target.getId());
                c.setAssignee_id(u.getId());
                c.setCreated_by(u.getId());
                c.setCreated_at(System.currentTimeMillis());

                target.getModeration().muted = true;
                target.getModeration().mute_end_time = totalMillis + System.currentTimeMillis();
                try {
                    target.save();
                    c.save();
                } catch (APIException e) {
                    e.printStackTrace();
                }
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
