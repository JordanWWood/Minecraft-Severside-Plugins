package network.marble.vanity.api.type;

import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.api.type.base.VanityItemBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public abstract class CyclingVanityItem extends VanityItemBase {
    private int task;
    private Slot slot;

    protected int interval;

    public static List<ItemStack> materials = new ArrayList<>();

    /**
     * Cycling vanity items cycle between items defined in the material list to create a
     * blinking or flickering style behaviour
     *
     * @param interval
     * @param p
     * @param slot
     */
    public CyclingVanityItem(int interval, Player p, Slot slot) {
        this.interval = interval;
        this.p = p;
        this.slot = slot;
    }

    @Override
    public void invoke(VanityPlugin pl) {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Vanity.getInstance(), this::run, 0L, interval);

        super.invoke(pl);
    }

    private int index = 0;
    @Override
    protected void run() {
        if (index >= materials.size()) index = 0;
        nextItem = materials.get(index);

        super.run();
        index++;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(task);
    }
}
