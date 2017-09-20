package network.marble.vanity.api.type.base;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;

public class VanityItemBase {

    @Getter protected ItemStack nextItem;
    @Getter protected Slot slot;
    @Getter protected ItemStack displayItem;
    /**
     * Pretty name. This is what vanity will use to display the item in the menu
     * Also use for sorting and searching.. don't remove it.
     *
     * @return the name of the item
     */
    @Getter protected String name;
    protected BukkitTask task;
    public VanityItemBase(Slot s, ItemStack display, String name) {
        this.slot = s;
        this.displayItem = display;
        this.name = name;
    }

    public void equipWriteTo(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            try {
                User u = new User().getByUUID(p.getUniqueId());
                VanityItem vi = new VanityItem().getByName(name);
                if(!u.getEquippedVanityItems().containsKey(slot.getValue())) {
                    u.getEquippedVanityItems().put(slot.getValue(), vi.getId());
                } else {
                    u.getEquippedVanityItems().replace(slot.getValue(), vi.getId());
                }
                System.out.println("VanityPlugin equip save: " + u.save());
            } catch (APIException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void equip(Player p) {
        Slot.equipTo(name, p);
    }
    
    protected void run(Player p) {
        p.updateInventory();
    }

    public void unEquip(Player p) {
        Slot.unequipFrom(name, p);
    }
    
    public void unEquipWriteTo(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            try {
                User u = new User().getByUUID(p.getUniqueId());
                u.getEquippedVanityItems().remove(slot.getValue());
                System.out.println("VanityPlugin unequip save: " + u.save());
            } catch (APIException e) {
                e.printStackTrace();
            }
        });
    }
}