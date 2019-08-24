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
    public SingleEquipVanityItem(Player player, ItemStack item, Slot s, String name) {
        super(player, s, name);
        this.p = player;
        this.nextItem = item;
    }

    @Override
    public void invoke() {
        run();
        super.invoke();
    }

    @Override
    protected void run() {
        super.run();
    }

    /**
     * method is invoked when a player disconnects or equips a new item in the slot this is equipped in
     */
    @Override
    public void cancel() {
        super.cancel();
    }
}
