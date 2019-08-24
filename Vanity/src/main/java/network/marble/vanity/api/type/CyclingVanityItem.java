package network.marble.vanity.api.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.type.base.VanityItemBase;

public abstract class CyclingVanityItem extends VanityItemBase {
    private BukkitTask task;

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
    public CyclingVanityItem(int interval, Player p, Slot slot, String name) {
        super(p, slot, name);
        this.interval = interval;
        this.p = p;
    }

    @Override
    public void invoke() {
        task = Bukkit.getScheduler().runTaskTimer(Vanity.getInstance(), this::run, 0L, interval);
        super.invoke();
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
        task.cancel();
        super.cancel();
    }
}
