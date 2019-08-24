package network.marble.vanity.api.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.type.base.VanityItemBase;

public abstract class LoopingVanityItem extends VanityItemBase {
    private BukkitTask task;
    private int interval;

    protected Player player;

    /**
     * Vanity Item that has unique behaviour that updates at specific intervals
     *
     * @param interval rate of update in ticks
     * @param player player that is effected
     */
    public LoopingVanityItem(int interval, Player player, Slot s, String name) {
        super(player, s, name);
        this.player = player;
        this.interval = interval;
    }

    @Override
    public void invoke() {
        task = Bukkit.getScheduler().runTaskTimer(Vanity.getInstance(), this::run, 0L, interval);
        super.invoke();
    }

    public abstract void run();

    /**
     * Override to add additional cleanup behaviour when a player either disconnects or equips another item in the slot
     */
    @Override 
    public void cancel() {
        task.cancel();
        super.cancel();
    }
}
