package network.marble.moderation.commands.buycraft;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderation.Moderation;
import network.marble.moderation.utils.ScheduleAsync;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class cmdSetRank extends Command {
    public cmdSetRank(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("buy.set.rank")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to run buycraft commands!"));
            return;
        }

        new ScheduleAsync(() -> {
            try {
                User u = new User().getByUsername(args[0]);

                if (!u.exists()) {
                    // TODO add command to list of scheduled changes for when the user eventually logs in for the first time
                } else {
                    if (u.getRank_id() == UUID.fromString(args[1])) return;
                    Rank r = new Rank().get(UUID.fromString(args[1]));

                    boolean f = false;
                    Rank p = new Rank().get(u.getRank_id());

                    while (p.exists()) {
                        if (p.getId().equals(r.getId())) {
                            f = true;
                            break;
                        }

                        p = new Rank().get(p.getParent_id());
                    }

                    if (r.exists() && !f) {
                        u.setRank_id(r.getId());
                        u.save();
                    }
                }
            } catch (APIException e) {
                e.printStackTrace();
            }
        }, 15L, TimeUnit.SECONDS);
    }
}
