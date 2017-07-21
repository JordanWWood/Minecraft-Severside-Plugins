package network.marble.vanity;

import lombok.Getter;
import network.marble.vanity.managers.EquipmentManager;
import network.marble.vanity.managers.VanityPluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import network.marble.vanity.managers.MenuManager;

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

        getLogger().info("Vanity successfully loaded.");
    }
}
