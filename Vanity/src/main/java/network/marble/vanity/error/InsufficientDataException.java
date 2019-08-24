package network.marble.vanity.error;

import lombok.Getter;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.vanity.Vanity;

public class InsufficientDataException extends Exception {
    @Getter private VanityItem vanityItem;
    @Getter private String missingField;
    @Getter private String version;

    public InsufficientDataException(VanityItem vanityItem, String missingField, String version) {
        this.vanityItem = vanityItem;
        this.missingField = missingField;
        this.version = version;

        Vanity.getInstance().getLogger().warning("Item " + vanityItem.getName() + " does not have the data " +
                "required to construct the item. Please make sure the " + missingField + " is populated for " +
                "Minecraft version " + version);
    }
}
