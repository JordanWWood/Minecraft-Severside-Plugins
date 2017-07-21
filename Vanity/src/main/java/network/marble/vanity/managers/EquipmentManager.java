package network.marble.vanity.managers;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentManager {
    @Getter static HashMap<UUID, Map<Slot, VanityItem>> playerEquipment = new HashMap<>();

    public EquipmentManager() {}
}