package network.marble.moderation;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import network.marble.moderation.commands.staff.discipline.cmdGoTo;
import network.marble.moderation.commands.staff.discipline.cmdKick;
import network.marble.moderation.commands.staff.discipline.cmdMute;
import network.marble.moderation.commands.staff.misc.cmdStaffChat;
import network.marble.moderation.commands.staff.ticket.cmdTicket;
import network.marble.moderation.listeners.PlayerListener;
import network.marble.moderation.punishment.PunishmentManager;

public class Moderation extends Plugin {
    @Getter @Setter private static Moderation instance;
    @Getter private static PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        instance = this;
        punishmentManager = new PunishmentManager();

        getProxy().getPluginManager().registerListener(this, new PlayerListener());

        // Commands
        // Staff
//        getProxy().getPluginManager().registerCommand(this, new cmdMute("Mute"));

        getProxy().getPluginManager().registerCommand(this, new cmdStaffChat("sc"));
        getProxy().getPluginManager().registerCommand(this, new cmdTicket("ticket"));
        getProxy().getPluginManager().registerCommand(this, new cmdKick("kick"));
        getProxy().getPluginManager().registerCommand(this, new cmdGoTo("goto"));

        this.getLogger().info("Moderation successfully loaded.");
    }
}
