package network.marble.vanity.api.base;

import java.util.UUID;

import network.marble.vanity.api.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.vanity.api.type.base.VanityItemBase;

public interface VanityPlugin {
    /**
     * Pretty name. This is what vanity will use to display the item in the menu
     * Also use for sorting and searching.. don't remove it.
     *
     * @return the name of the item
     */
    String getName();

    void onEquip(Player player);
    void onRemove(Player player);
    /**
     *
     * @return the item to display in the vanity inventory
     */
    ItemStack getItemStack();
    Slot getSlot();
}