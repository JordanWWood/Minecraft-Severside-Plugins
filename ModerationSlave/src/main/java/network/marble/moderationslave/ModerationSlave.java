package network.marble.moderationslave;

import lombok.Getter;
import network.marble.moderationslave.commands.cmdPunishment;
import network.marble.moderationslave.utils.Schematic;
import network.marble.moderationslave.utils.SchematicLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

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

        this.getCommand("punishment").setExecutor(new cmdPunishment());
    }

    static public String exportResource(String resourceName) throws Exception {
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
