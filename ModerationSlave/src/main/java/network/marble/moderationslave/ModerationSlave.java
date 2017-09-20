package network.marble.moderationslave;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.ChatFilterExpression;
import network.marble.moderationslave.commands.cmdMute;
import network.marble.moderationslave.commands.cmdPunishment;
import network.marble.moderationslave.listeners.PlayerListener;
import network.marble.moderationslave.utils.Schematic;
import network.marble.moderationslave.utils.SchematicLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModerationSlave extends JavaPlugin {
    @Getter private static ModerationSlave instance;
    @Getter private static Schematic schematic;

    @Override
    public void onEnable() {
        instance = this;

        try {
            exportResource("/court.schematic");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            schematic = SchematicLoader.loadSchematic("plugins/ModerationSlave/court.schematic");
        } catch (IOException e) {
            e.printStackTrace();
        }

        getCommand("punishment").setExecutor(new cmdPunishment());
        getCommand("mute").setExecutor(new cmdMute());

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        try {
            List<String> expressions = new ArrayList<>();
            new ChatFilterExpression().get().forEach(i -> expressions.add(i.getExpression()));
            PlayerListener.regexStrings.addAll(expressions);
        } catch (APIException e) {
            e.printStackTrace();
        }
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
