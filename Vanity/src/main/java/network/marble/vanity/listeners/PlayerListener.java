package network.marble.vanity.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.type.base.VanityItemBase;
import network.marble.vanity.managers.EquipmentManager;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            User u = null;
            try {
                u = new User().getByUUID(e.getPlayer().getUniqueId());
            } catch (APIException ex) {
                ex.printStackTrace();
                return;
            }
            if (u.getEquippedVanityItems().isEmpty()) return;
            for(UUID id : u.getEquippedVanityItems().values()) {
                VanityItem vi = null;
                try {
                    vi = new VanityItem().get(id);
                } catch (APIException ex) {
                    ex.printStackTrace();
                }
                VanityItemBase pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());
                if(pl == null) continue;
                Bukkit.getScheduler().runTask(Vanity.getInstance(), () -> pl.equip(e.getPlayer()));
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            if (EquipmentManager.getPlayerEquipment().get(e.getPlayer().getUniqueId()).isEmpty()) return;
            for (String entry : EquipmentManager.getPlayerEquipment().get(e.getPlayer().getUniqueId()).keySet()) {
                VanityItemBase pl = Vanity.getVanityPluginManager().getPlugins().get(entry);
                Bukkit.getScheduler().runTask(Vanity.getInstance(), () -> pl.unEquip(e.getPlayer()));
            }

        });
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        e.setCancelled(true);
    }
}
