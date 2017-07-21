package network.marble.vanity.menus.executors;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.base.VanityPlugin;
import org.bukkit.entity.Player;

public class VanityItemEquipExecutor implements ActionExecutor {
    VanityPlugin pl;

    public VanityItemEquipExecutor(VanityPlugin pl) {
        this.pl = pl;
    }

    @Override
    public void executeAction(Player player, InventoryItem inventoryItem, String[] strings) {
        Vanity.getInstance().getLogger().info(pl.getMaterial().name() + " has been equipped by " + player.getName());

        pl.onEquip(player);
    }
}
