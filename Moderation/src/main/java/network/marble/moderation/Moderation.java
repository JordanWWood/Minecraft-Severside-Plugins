package network.marble.moderation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import network.marble.dataaccesslayer.bungee.DataAccessLayer;

import network.marble.moderation.commands.buycraft.cmdAddVanity;
import network.marble.moderation.commands.buycraft.cmdSetRank;
import network.marble.moderation.commands.staff.discipline.cmdGoTo;
import network.marble.moderation.commands.staff.discipline.cmdKick;
import network.marble.moderation.commands.staff.misc.cmdStaffChat;
import network.marble.moderation.commands.staff.ticket.cmdTicket;
import network.marble.moderation.listeners.CustomPayloadListener;
import network.marble.moderation.listeners.PlayerListener;
import network.marble.moderation.punishment.PunishmentManager;
import network.marble.moderation.punishment.communication.RabbitManager;

import java.io.IOException;

public class Moderation extends Plugin {
    @Getter @Setter private static Moderation instance;
    @Getter private static PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        instance = this;
        punishmentManager = new PunishmentManager();

        RabbitManager.startQueueConsumer();
        RabbitManager.bindKey("staff.all");

        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        getProxy().getPluginManager().registerListener(this, new CustomPayloadListener());

        // Staff
        getProxy().getPluginManager().registerCommand(this, new cmdStaffChat("sc"));
        getProxy().getPluginManager().registerCommand(this, new cmdTicket("ticket"));
        getProxy().getPluginManager().registerCommand(this, new cmdKick("kick"));
        getProxy().getPluginManager().registerCommand(this, new cmdGoTo("goto"));

        // buycraft
        getProxy().getPluginManager().registerCommand(this, new cmdSetRank("setrank"));
        getProxy().getPluginManager().registerCommand(this, new cmdAddVanity("addvanity"));

        this.getLogger().info("Moderation successfully loaded.");
    }
}
