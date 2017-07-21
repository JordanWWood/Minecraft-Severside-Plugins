package network.marble.dataaccesslayer.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;

public class DataAccessLayer extends JavaPlugin
{
    @Getter
    private static network.marble.dataaccesslayer.base.DataAccessLayer instance;
    
    @Getter @Setter private static String serverName = null;

    @Override
    public void onEnable() {
        instance = new network.marble.dataaccesslayer.base.DataAccessLayer(getLogger());
        instance.onEnable();
        GetServerName l = new GetServerName(this);
        getServer().getPluginManager().registerEvents(l, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", l);
    }

    @Override
    public void onDisable() {
        instance.onDisable();
        instance = null;
    }
}
