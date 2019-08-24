package network.marble.vanity.error;

import lombok.Getter;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;

public class VersionNotFoundException extends Exception {
    @Getter private String version;
    @Getter private VanityItem vanityItem;

    public VersionNotFoundException(String version, VanityItem vanityItem) {
        this.version = version;
        this.vanityItem = vanityItem;

        Vanity.getInstance().getLogger().warning("Item " + vanityItem.getName() + " does not have a relevant mapping for Minecraft version " + version);
    }
}
