package network.marble.vanity.api.type.base;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.vanity.api.Slot;

public abstract class VanityItemBase {

    @Getter protected ItemStack nextItem;
    @Getter protected Slot slot;
    protected String name;
    protected Player p;
    
    public VanityItemBase(Player p, Slot s, String name) {
        this.p = p;
        this.slot = s;
        this.name = name;
    }

    public void invoke() {
        Slot.equipTo(name, nextItem, p);
    }

    protected void run() {
        InventoryAPI.refreshPlayerArmour(p, slot.getArmorType());
    }

    public void cancel() {
        Slot.unequipFrom(name, p);
    }
}
