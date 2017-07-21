package network.marble.vanity.menus.getters;

import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.managers.EquipmentManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by jorda_000 on 22/06/2017.
 */
public class WornItemGetter implements ItemStackGetter {
    private Slot slot;

    public WornItemGetter(Slot slot) {
        this.slot = slot;
    }

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        VanityItem vi = EquipmentManager.getPlayerEquipment().get(player.getUniqueId()).get(slot);
        VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());

        return pl.getVanityItem().getNextItem();
    }
}
