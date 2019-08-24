package network.marble.quickqueue.menus.getters;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LangGetter implements ItemStackGetter{
    private final ItemStack icon;
    private final String phrasePrefix;
    private final boolean hasLore;

    /**
     * Sets up the phrase to be used for an item, e.g 'icon.name' will be used
     * for the icon title as 'icon.name.tag' and the lore as 'icon.name.lore'
     * @param phrasePrefix The phrase prefix to use
     * @param hasLore Whether to render lore or not
     */
    public LangGetter(ItemStack icon, String phrasePrefix, boolean hasLore){
        this.icon = icon;
        this.phrasePrefix = phrasePrefix;
        this.hasLore = hasLore;
    }

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        return InventoryAPI.appendLore(
                InventoryAPI.renameItemstack(
                        icon,
                        Lang.get(phrasePrefix + ".tag", player)
                ),
                hasLore ? Lang.get(phrasePrefix + ".lore", player).split("\\\\n") : null
        );
    }
}
