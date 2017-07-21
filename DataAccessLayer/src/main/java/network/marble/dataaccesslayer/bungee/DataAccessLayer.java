package network.marble.dataaccesslayer.bungee;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class DataAccessLayer extends Plugin
{
    @Getter
    private static network.marble.dataaccesslayer.base.DataAccessLayer instance;

    @Override
    public void onEnable() {
        instance = new network.marble.dataaccesslayer.base.DataAccessLayer(getLogger());
        instance.onEnable();
    }

    @Override
    public void onDisable() {
        instance.onDisable();
        instance = null;
    }
}
