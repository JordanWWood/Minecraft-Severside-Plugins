package network.marble.vanity.api.type;

import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.api.type.base.VanityItemBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class SingleEquipVanityItem extends VanityItemBase {
    /**
     * Runs only once to equip an item to player
     *
     * @param player
     */
    public SingleEquipVanityItem(Player player, ItemStack item) {
        this.p = player;
        this.nextItem = item;
    }

    @Override
    public void invoke(VanityPlugin pl) {
        run();

        super.invoke(pl);
    }

    @Override
    protected void run() {
        super.run();
    }

    /**
     * method is invoked when a player disconnects or equips a new item in the slot this is equipped in
     */
    @Override
    public abstract void cancel();
}
