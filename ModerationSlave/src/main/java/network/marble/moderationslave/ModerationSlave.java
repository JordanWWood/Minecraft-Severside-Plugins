package network.marble.moderationslave;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import network.marble.hecate.Hecate;
import network.marble.moderationslave.commands.Teleport;
import network.marble.moderationslave.communication.RabbitManager;
import network.marble.moderationslave.managers.CustomPayloadManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import network.marble.moderationslave.commands.Mute;
import network.marble.moderationslave.commands.PunishmentCommand;
import network.marble.moderationslave.listeners.PlayerListener;
import network.marble.moderationslave.utils.Schematic;
import network.marble.moderationslave.utils.SchematicLoader;

public class ModerationSlave extends JavaPlugin {
    @Getter private static ModerationSlave instance;
    @Getter private static Schematic schematic;
    @Getter private static CustomPayloadManager customPayloadManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            exportResource("/court.schematic");
            schematic = SchematicLoader.loadSchematic("plugins/ModerationSlave/court.schematic");
        } catch (Exception e) {
            e.printStackTrace();
        }

        customPayloadManager = new CustomPayloadManager();
        RabbitManager.startQueueConsumer();

        getCommand("punishment").setExecutor(new PunishmentCommand());
        getCommand("mute").setExecutor(new Mute());

        if (Hecate.getServerName().startsWith("HUB")) {
            getCommand("teleport").setExecutor(new Teleport());
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    static public String exportResource(String resourceName) throws Exception {
        instance.getDataFolder().mkdir();
        try (OutputStream resStreamOut = new FileOutputStream(instance.getDataFolder() + resourceName); InputStream stream = ModerationSlave.class.getResourceAsStream(resourceName)){
            //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null)
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");

            int readBytes;
            byte[] buffer = new byte[4096];
    
            if (!instance.getDataFolder().exists())
                instance.getDataFolder().mkdir();
    
            while ((readBytes = stream.read(buffer)) > 0)
                resStreamOut.write(buffer, 0, readBytes);
        } catch (Exception ex) {
            throw ex;
        }

        return resourceName;
    }
}
