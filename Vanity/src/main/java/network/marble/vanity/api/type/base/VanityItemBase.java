package network.marble.vanity.api.type.base;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.api.SlotCollisionException;
import network.marble.inventoryapi.enums.ArmorType;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.StandardItemStack;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.managers.EquipmentManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class VanityItemBase {
    @Getter protected ItemStack nextItem;
    protected Slot slot;
    protected Player p;

    public void invoke(VanityPlugin pl) {
        VanityItem vi = null;
        User u = null;

        try {
            vi = new VanityItem().getByName(pl.getName());
            u = new User().getByUUID(p.getUniqueId());
        } catch (APIException e) {
            e.printStackTrace();
        }

        if(u.getEquippedVanityItems().containsKey(slot.getValue())) {
            u.getEquippedVanityItems().replace(slot.getValue(), vi.getId());
        } else {
            u.getEquippedVanityItems().put(slot.getValue(), vi.getId());
        }
    }

    protected void run() {
        InventoryAPI.refreshPlayerArmour(p, slot.getArmorType());
    }

    public abstract void cancel();
}
