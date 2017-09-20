package network.marble.vanity.api.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.vanity.api.Slot;
import network.marble.vanity.api.type.base.VanityItemBase;

public abstract class SingleEquipVanityItem extends VanityItemBase {
    /**
     * Runs only once to equip an item to player
     *
     * @param player
     */
    public SingleEquipVanityItem(ItemStack item, Slot s, String name) {
    	super(s, item, name);
    }

    public void equip(Player p) {
        run(p);
        super.equip(p);
    }

    @Override
    protected void run(Player p) {
        super.run(p);
    }

    /**
     * method is invoked when a player disconnects or equips a new item in the slot this is equipped in
     */
    @Override
    public void unEquip(Player p) {
        super.unEquip(p);
    }
}
