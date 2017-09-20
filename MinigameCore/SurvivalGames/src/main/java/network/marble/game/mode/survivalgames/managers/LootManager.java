package network.marble.game.mode.survivalgames.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.game.mode.survivalgames.config.MapConfig;
import network.marble.game.mode.survivalgames.config.Model.Locations;
import network.marble.game.mode.survivalgames.config.Model.TierItem;
import network.marble.game.mode.survivalgames.listeners.StateListener;
import network.marble.minigamecore.managers.AnalyticsManager;
import network.marble.minigamecore.managers.PlayerManager;

public class LootManager {
    private static MapConfig mapConfig = StateListener.getMapConfig();
    private static Map<Location, Boolean> openedChests = new HashMap<>();
    private static Map<Player, Map<Location, Boolean>> playerOpenedChests = new HashMap<>();

    public static void rollLoot(Locations location, int minItems, int maxItems, boolean largeChest, Player p) {
    	World world = p.getWorld();
        int tier = location.getTier();
        List<TierItem> loot = mapConfig.getChests().getTiers().get(tier);
        int numberOfItems = random(minItems, (largeChest ? maxItems * 2 : maxItems), null);

        Chest chest = (Chest) (new Location(world, location.getX(), location.getY(), location.getZ())).getBlock().getState();

        if(openedChests.containsKey(chest.getLocation())) return;
        chest.getInventory().clear();

        for (int i = 0; i < numberOfItems; i++) {
            List<Integer> usedSlots = new ArrayList<>();

            // Chest sizes, I know I know magic numbers fuck off
            int slot = random(0, (largeChest ? 54 - 1 : 27 - 1), usedSlots);
            int random = random(0, loot.size() - 1, null);

            TierItem item = loot.get(random);
            usedSlots.add(slot);

            ItemStack itemStack = new ItemStack(Material.valueOf(item.getType().toUpperCase()), item.getAmount());
            chest.getInventory().setItem(slot, itemStack);
        }

        if(!playerOpenedChests.containsKey(p)) playerOpenedChests.put(p, new HashMap<>());
        openedChests.put(chest.getLocation(), true);
        if(!playerOpenedChests.get(p).containsKey(chest.getLocation())){
        	UUID pDBID = PlayerManager.getPlayer(p).getUserId();
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(pDBID, StateListener.getMapConfig().getInfo().getName() + "chestsopened", 1);
    		AnalyticsManager.getInstance().alterGameModeAnalyticsValue(pDBID, "chestsopened", 1);
    		playerOpenedChests.get(p).put(chest.getLocation(), true);
        }
    }

    private static int random(int min, int max, List<Integer> excludedNumbers) {//TODO reannotate nullable on list
        int random = ThreadLocalRandom.current().nextInt(min, max + 1);

        if (excludedNumbers != null) {
            for (int number : excludedNumbers) {
                if (number == random) {
                    return random(min, max, excludedNumbers);
                }
            }
        }

        return random;
    }

    public static void refillChests() {
        openedChests.clear();
        playerOpenedChests.clear();
    }
}
