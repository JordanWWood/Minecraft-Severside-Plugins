package network.marble.vanity.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.managers.EquipmentManager;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Vanity.getInstance().getLogger().info("PlayerJoinEvent");
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            User u = null;
            try {
                u = new User().get(e.getPlayer().getUniqueId());
            } catch (APIException ex) {
                ex.printStackTrace();
                return;
            }

            if (u.getEquippedVanityItems().isEmpty()) return;
            for(Map.Entry<Integer, UUID> entry : u.getEquippedVanityItems().entrySet()) {
                VanityItem vi = null;
                try {
                    vi = new VanityItem().get(entry.getValue());
                    VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());
                    pl.onEquip(e.getPlayer());
                } catch (Exception ex) {
                    u.getEquippedVanityItems().remove(entry.getKey());
                    try {
                        u.save();
                    } catch (APIException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Vanity.getInstance().getLogger().info("PlayerQuitEvent");
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            Map<String, ItemStack> t = EquipmentManager.getPlayerEquipment().get(e.getPlayer().getUniqueId());
            if (t == null || t.isEmpty()) return;
            User u = null;
            List<Integer> usedSlots = new ArrayList<>();
            for (String entry : EquipmentManager.getPlayerEquipment().get(e.getPlayer().getUniqueId()).keySet()) {
                VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(entry);
                Slot slot = pl.getSlot();
                usedSlots.add(slot.getValue());
                try {
                    u = new User().getByUUID(e.getPlayer().getUniqueId());
                    VanityItem item = new VanityItem().getByName(pl.getName());
                    if(!u.getEquippedVanityItems().containsKey(slot.getValue())) {
                        u.getEquippedVanityItems().put(slot.getValue(), item.getId());
                    } else {
                        u.getEquippedVanityItems().replace(slot.getValue(), item.getId());
                    }
                    u.save();
                } catch (APIException ex) {
                    ex.printStackTrace();
                }
                // removes slots that are not used/updated from the DB
                for(Iterator<Integer> iter = u.getEquippedVanityItems().keySet().iterator(); iter.hasNext();) {
                    int i = iter.next();
                    if(!usedSlots.contains(i)) {
                        iter.remove();
                        try {
                            u.save();
                        } catch (APIException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                Bukkit.getScheduler().runTask(Vanity.getInstance(), () -> pl.onRemove(e.getPlayer()));
            }

        });
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        e.setCancelled(true);
    }
}
