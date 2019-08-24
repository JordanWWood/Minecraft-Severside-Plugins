package network.marble.vanity.internal;

import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.api.type.base.VanityItemBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DatabaseItem implements VanityPlugin {
    private String name;
    private ItemStack itemStack;
    private Slot slot;

    DatabaseEquipVanityItem item = null;

    public DatabaseItem(String name, ItemStack itemStack, Slot slot) {
        this.name = name;
        this.itemStack = itemStack;
        this.slot = slot;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onEquip(Player player) {
        item = new DatabaseEquipVanityItem(player, itemStack, slot, name);
        item.invoke();
    }

    @Override
    public void onRemove(Player player) {
        Slot.unequipFrom(name, player, slot);
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public Slot getSlot() {
        return slot;
    }
}
