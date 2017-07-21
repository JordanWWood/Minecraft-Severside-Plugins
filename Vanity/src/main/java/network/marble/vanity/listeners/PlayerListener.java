package network.marble.vanity.listeners;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.vanity.VanityItem;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.api.base.VanityPlugin;
import network.marble.vanity.managers.EquipmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Map<Slot, VanityItem> itemMap = new HashMap<>();

        User u = null;
        try {
            u = new User().getByUUID(e.getPlayer().getUniqueId());
        } catch (APIException ex) {
            ex.printStackTrace();
        }

        // TODO load already equiped vanity items
        for (Map.Entry<Integer, UUID> entry : u.getEquippedVanityItems().entrySet()) {
            Slot slot = Slot.getByValue(entry.getKey());

            VanityItem vi = null;
            try {
                vi = new VanityItem().get(entry.getValue());
            } catch (APIException ex) {
                ex.printStackTrace();
            }

            itemMap.put(slot, vi);

            VanityPlugin pl = Vanity.getVanityPluginManager().getPlugins().get(vi.getName());
            pl.onEquip(e.getPlayer());
        }
        EquipmentManager.getPlayerEquipment().put(e.getPlayer().getUniqueId(), itemMap);
    }
}
