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
    public CyclingVanityItem(Player pl, int interval, Slot slot, String name) {
    	super(slot, materials.get(0), name);
        this.interval = interval;
    }

    public void equip(Player player) {
        task = Bukkit.getScheduler().runTaskTimer(Vanity.getInstance(), () -> this.run(player), 0L, interval);
        super.equip(player);
    }

    private int index = 0;
    
    @Override
    protected void run(Player player) {
        if (index >= materials.size()) index = 0;
        nextItem = materials.get(index);

        super.run(player);
        index++;
    }

    public void unEquip(Player player) {
        task.cancel();
        super.unEquip(player);
    }
}
