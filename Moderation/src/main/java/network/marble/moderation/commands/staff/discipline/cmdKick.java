package network.marble.moderation.commands.staff.discipline;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderation.Moderation;

/**
 * Created by jorda_000 on 19/08/2017.
 */
public class cmdKick extends Command {

    public cmdKick(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(Moderation.getInstance(), () -> {
            User u = null;
            try {
                u = new User().getByUUID(((ProxiedPlayer) sender).getUniqueId());
            } catch (APIException e) {
                e.printStackTrace();
            }

            if (u.hasPermission("moderation.kick")) {
                if (args.length < 1) {
                    ((ProxiedPlayer) sender).sendMessage("Incorrect args! /kick (target) (reason...)");
                    return;
                }

                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
                if (p != null) {
                    p.disconnect(new TextComponent("You have been kicked!"));

                    User target = null;
                    try {
                        target = new User().getByUUID(p.getUniqueId());
                    } catch (APIException e) {
                        e.printStackTrace();
                    }

                    Case c = new Case();
                    c.setOutcome(CaseOutcome.Kicked);
                    c.setCreated_by(u.getUuid());
                    c.setAssignee_id(u.getUuid());
                    c.setCreated_at(System.currentTimeMillis());
                    c.setJudgementee_id(target.getUuid());

                    StringBuilder builder = new StringBuilder();
                    for (String s : args) {
                        if (s.equalsIgnoreCase(args[0]))
                            continue;

                        builder.append(s + " ");
                    }

                    c.setDescription(builder.toString());
                    c.setClosed_at(System.currentTimeMillis());
                    c.setPardoned(false);
                    c.setOutcome_duration(0L);

                    try {
                        c = c.saveAndReturn();
                    } catch (APIException e) {
                        e.printStackTrace();
                    }

                    p.disconnect(new TextComponent("Kicked by a member staff!"));

                    TextComponent message = new TextComponent(target.getName() + " has been kicked. The case is located ");
                    TextComponent link = new TextComponent("HERE");

                    //TODO change to get panel URL from database
                    link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://panel.minevibe.net/cases/" + c.getId()));
                    link.setUnderlined(true);
                    link.setBold(true);
                    link.setColor(ChatColor.GOLD);

                    message.setColor(ChatColor.GRAY);
                    message.addExtra(link);
                    message.addExtra(".");

                    sender.sendMessage(message);
                }
            }
        });
    }
}
