package network.marble.vanity.api.type;

import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.api.type.base.VanityItemBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class LoopingVanityItem extends VanityItemBase {
    private int task;
    private int interval;

    protected Player player;

    /**
     * Vanity Item that has unique behaviour that updates at specific intervals
     *
     * @param interval rate of update in ticks
     * @param player player that is effected
     */
    public LoopingVanityItem(int interval, Player player) {
        this.player = player;
        this.interval = interval;
    }

    @Override
    public void invoke(VanityPlugin pl) {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Vanity.getInstance(), this::run, 0L, interval);

        super.invoke(pl);
    }

    public abstract void run();

    /**
     * Override to add additional cleanup behaviour when a player either disconnects or equips another item in the slot
     */
    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(task);
    }
}
