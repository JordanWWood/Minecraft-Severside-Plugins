package network.marble.vanity.api.base;

import network.marble.vanity.Vanity;
import network.marble.vanity.api.type.base.VanityItemBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;

public interface VanityPlugin {
    /**
     * Pretty name. This is what vanity will use to display the item in the menu
     *
     * @return the name of the item
     */
    String getName();

    void onEquip(Player player);
    void onRemove(Player player);

    Material getMaterial();
    VanityItemBase getVanityItem();

    int getQuantity();
    short getDamage();
}
