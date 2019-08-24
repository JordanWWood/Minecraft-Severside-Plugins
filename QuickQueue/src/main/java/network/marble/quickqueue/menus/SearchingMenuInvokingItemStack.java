package network.marble.quickqueue.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.itemstacks.InventoryItem;

public class SearchingMenuInvokingItemStack extends InventoryItem{
    private ItemStack itemStack;
    private String inventoryName = null;
    int minimumCharacters;

    public SearchingMenuInvokingItemStack(ItemStack itemStack, int minimumCharacters) {
        this(itemStack, minimumCharacters, "Search");
    }

    public SearchingMenuInvokingItemStack(ItemStack itemStack, int minimumCharacters, String inventoryName) {
        super(itemStack);
        this.itemStack = itemStack;
        this.inventoryName = inventoryName;
        this.minimumCharacters = minimumCharacters;
    }

    public ItemStack getOriginItemStack(){
        return itemStack;
    }

    public String getInventoryName(){
        return inventoryName;
    }

    @Override
    public void execute(Player p, int slot) {
        InventoryAPI.openMenuForPlayer(p.getUniqueId(), new SearchingMenu(p, Menus.inviteMember, InventoryType.ANVIL.getDefaultSize(), minimumCharacters, inventoryName));
    }
}
