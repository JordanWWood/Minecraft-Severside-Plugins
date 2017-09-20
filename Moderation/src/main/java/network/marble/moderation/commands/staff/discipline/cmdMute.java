package network.marble.moderation.commands.staff.discipline;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderation.Moderation;
import network.marble.moderation.utils.FontFormat;
import network.marble.moderation.utils.Time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class cmdMute extends Command {
    public cmdMute(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (sender instanceof ProxiedPlayer){
            if (sender.hasPermission("moderation.commands.mute")){
                if (strings.length > 2){
                    Long totalMillis = 0L;
                    int length = 0;

                    ProxiedPlayer p = Moderation.getInstance().getProxy().getPlayer(strings[0]);
                    User u = null;

                    try {
                        u = new User().get(p.getUniqueId());
                    } catch (APIException e) {
                        e.printStackTrace();
                    }

                    if (u == null) {
                        sender.sendMessage(FontFormat.translateString("&cUser " + strings[0] + "could not be found"));
                        return;
                    }

                    for (int i = 1; i < strings.length; i++){
                        if (strings[1].equalsIgnoreCase("permanent")){
                            totalMillis = 157784630000L;
                            break;
                        }

                        String time = strings[i].replaceAll("[^A-Za-z]+", "");

                        if (time.equalsIgnoreCase("d")){
                            totalMillis += Time.DAY.getMillis() * getPeriod(strings[i]);
                        } else if (time.equalsIgnoreCase("h")){
                            totalMillis += Time.HOUR.getMillis() * getPeriod(strings[i]);
                        } else if (time.equalsIgnoreCase("m")){
                            totalMillis += Time.MINUTE.getMillis() * getPeriod(strings[i]);
                        } else {
                            length = i;
                            break;
                        }
                    }

                    String reason = "";
                    for (int i = length; i < strings.length; i++) reason += strings[i];

                    //TODO: Get teken todo
//					ModerationLog m = new ModerationLog(user.getUuid(), u.getUuid(), System.currentTimeMillis(), "MUTE", reason, totalMillis + System.currentTimeMillis());
//					DAOManager.getModerationLogDAO().save(m);

                    u.getModeration().mute_end_time = totalMillis + System.currentTimeMillis();

                    sender.sendMessage(FontFormat.translateString("&aUser " + strings[0] + " has been muted"));
                } else {
                    sender.sendMessage(FontFormat.translateString("Incorrect perameters! /mute (user) (amount of time) (reason...)"));
                }
            } else {
                sender.sendMessage(FontFormat.translateString("TODO"));
            }
        } else {
            sender.sendMessage(FontFormat.translateString("TODO"));
        }
    }

    public long getPeriod(String s){
        String number = "";

        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(s);
        while(m.find())
        {
            number += m.group(1);
        }

        return Long.parseLong(number);
    }
}
