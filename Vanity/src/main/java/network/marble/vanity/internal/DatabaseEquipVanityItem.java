package network.marble.vanity.internal;

import network.marble.vanity.api.Slot;
import network.marble.vanity.api.type.SingleEquipVanityItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DatabaseEquipVanityItem extends SingleEquipVanityItem {
    public DatabaseEquipVanityItem(Player player, ItemStack item, Slot s, String name) {
        super(player, item, s, name);
    }
}
