package network.marble.moderation.commands.staff.discipline;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.dataaccesslayer.models.user.User;

import java.util.Objects;

public class cmdKick extends Command {

    public cmdKick(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        User u = null;
        try {
            u = new User().getByUUID(((ProxiedPlayer)sender).getUniqueId());
        } catch (APIException e) {
            e.printStackTrace();
        }

        if (u.hasPermission("moderation.kick")) {

            if (args.length < 1 && !(args.length > 1)) {
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
                c.setCreated_by(u.getId());
                c.setAssignee_id(u.getId());
                c.setCreated_at(System.currentTimeMillis());
                c.setJudgementee_id(target.getId());

                StringBuilder builder = new StringBuilder();
                for (String s : args) {
                    if (Objects.equals(s.toUpperCase(), args[0].toUpperCase())) {
                        continue;
                    }

                    builder.append(s + " ");
                }

                c.setDescription(builder.toString());
                c.setClosed_at(System.currentTimeMillis());
                c.setPardoned(false);
                c.setOutcome_duration(0L);

                try {
                    c.save();
                } catch (APIException e) {
                    e.printStackTrace();
                }

                ((ProxiedPlayer) sender).sendMessage(new TextComponent(target.getName() + " has been removed from the server!"));
                p.disconnect(new TextComponent("Kicked by a member of staff! "));
            }
        }
    }
}
