package network.marble.quickqueue.menus.getters;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messagelibrary.api.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Gets the icon for games found in the game selector.
 * Uses {@link Lang Lang} phrases for the name and description.
 */
public class GameMenuIconGetter implements ItemStackGetter {
    private String namePhrase, descriptionPhrase;
    private Material material;
    @Getter private ItemStack defaultIcon;

    /**
     * Creates a GameMenuIconGetter
     * @param namePhrase The lang phraseId for the game name
     * @param descriptionPhrase The lang phraseId for the game description
     * @param material The material to set as the item
     */
    public GameMenuIconGetter(String namePhrase, String descriptionPhrase, String material){
        this.namePhrase = namePhrase;
        this.descriptionPhrase = descriptionPhrase;
        this.material = Material.valueOf(material);
        defaultIcon = InventoryAPI.renameItemstack(new ItemStack(this.material), Lang.translatePhrase(namePhrase, Lang.ENGLISH));
    }

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(Lang.get(namePhrase, player));
        String loreRaw = ChatColor.RESET + Lang.get(descriptionPhrase, player);//Reset text at start to prevent the ugly default lore styling
        List<String> lore = Arrays.asList(loreRaw.split("\\\\n"));//Multiple backslashes due to api interference
        meta.setLore(lore);

        is.setItemMeta(meta);

        return is;
    }
}
