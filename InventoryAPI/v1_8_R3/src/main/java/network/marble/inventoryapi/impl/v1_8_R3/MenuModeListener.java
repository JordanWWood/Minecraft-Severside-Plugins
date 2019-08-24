package network.marble.inventoryapi.impl.v1_8_R3;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public class MenuModeListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemUse(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if(p.getGameMode() != GameMode.CREATIVE){
            Action action = event.getAction();
            boolean cancel = true;
            if(action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK){
                switch(event.getClickedBlock().getType()){
                    case ACACIA_DOOR:
                    case BED_BLOCK:
                    case BIRCH_DOOR:
                    case BREWING_STAND:
                    case BURNING_FURNACE:
                    case CHEST:
                    case DARK_OAK_DOOR:
                    case ENCHANTMENT_TABLE:
                    case FENCE_GATE:
                    case FURNACE:
                    case JUKEBOX:
                    case JUNGLE_DOOR:
                    case LEVER:
                    case NOTE_BLOCK:
                    case SPRUCE_DOOR:
                    case STONE_BUTTON:
                    case TRAPPED_CHEST:
                    case TRAP_DOOR:
                    case TRIPWIRE:
                    case WOOD_BUTTON:
                    case WOODEN_DOOR:
                    case WORKBENCH: cancel = false; break;
                    default: break;
                }
            }

            if(p.getGameMode() == GameMode.ADVENTURE && action == Action.LEFT_CLICK_AIR) cancel = false; //Workaround for adventure mode messing with the interact event

            if(cancel && action != Action.PHYSICAL){
                event.setCancelled(cancel);
                PlayerInventory inv = p.getInventory();
                int slot = inv.getHeldItemSlot();
                network.marble.inventoryapi.listeners.MenuModeListener.processClick(p, inv.getItem(slot), slot, inv.getHeldItemSlot());
            }
        }
    }
}
