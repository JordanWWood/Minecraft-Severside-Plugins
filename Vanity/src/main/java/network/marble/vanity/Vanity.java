package network.marble.vanity;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import network.marble.vanity.listeners.PlayerListener;
import network.marble.vanity.managers.EquipmentManager;
import network.marble.vanity.managers.MenuManager;
import network.marble.vanity.managers.VanityPluginManager;

public class Vanity extends JavaPlugin {
    @Getter private static Vanity instance;

    @Getter private static VanityPluginManager vanityPluginManager;
    @Getter private static MenuManager menuManager;
    @Getter private static EquipmentManager equipmentManager;
    
    @Override
    public void onEnable() {
        instance = this;

        vanityPluginManager = new VanityPluginManager();
        menuManager = new MenuManager();
        equipmentManager = new EquipmentManager();

        menuManager.buildInventoryMenus();

        // What is life?
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getLogger().info("Vanity successfully loaded.");

        instance.getDataFolder().mkdir();
    }
}