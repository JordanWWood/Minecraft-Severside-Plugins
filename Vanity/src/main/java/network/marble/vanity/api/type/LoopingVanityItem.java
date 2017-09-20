package network.marble.vanity.api.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.type.base.VanityItemBase;

public abstract class LoopingVanityItem extends VanityItemBase {
    private BukkitTask task;
    private int interval;

    /**
     * Vanity Item that has unique behaviour that updates at specific intervals
     *
     * @param interval rate of update in ticks
     * @param player player that is effected
     */
    public LoopingVanityItem(int interval, Slot s, String name, ItemStack display) {
    	super(s, display, name);
        this.interval = interval;
    }

    @Override
    public void equip(Player p) {
        task = Bukkit.getScheduler().runTaskTimer(Vanity.getInstance(), () -> run(p), 0L, interval);
        super.equip(p);
    }
    
    @Override
    public void run(Player p) {
    	super.run(p);
    }

    /**
     * Override to add additional cleanup behaviour when a player either disconnects or equips another item in the slot
     */
    @Override 
    public void unEquip(Player p) {
        task.cancel();
        super.unEquip(p);
    }
}
