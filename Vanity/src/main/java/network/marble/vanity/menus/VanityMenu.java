package network.marble.vanity.menus;

import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.messageapi.api.FontFormat;
import network.marble.messagelibrary.api.Lang;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.Slot;
import network.marble.vanity.managers.EquipmentManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class VanityMenu extends Menu {
    private final int INVENTORY_SIZE = 54;

    private ItemStack[] itemStacks = new ItemStack[INVENTORY_SIZE];
    private InventoryItem[] inventoryItems = new InventoryItem[INVENTORY_SIZE];
    private Player player;

    private int page = 0;
    private String catagory;
    private Slot slot;

    private List<InventoryItem> vanityItems;
    private Inventory inv = null;

    public VanityMenu(Player targetPlayer, InventoryItem inventoryItem, int inventorySize, List<InventoryItem> vanityItems, String category) {
        super(targetPlayer, inventoryItem, inventorySize);

        Inventory inv = Bukkit.createInventory(targetPlayer, 54, category);
        targetPlayer.openInventory(inv);

        this.inv = inv;

        updateButtons();
        loadMenu(targetPlayer, vanityItems, category);
    }

    private void updateButtons() {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            for (int i = INVENTORY_SIZE - 9; i < INVENTORY_SIZE; i++) {
                if (i == INVENTORY_SIZE - 6) {
                    itemStacks[i] = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
                    ItemMeta meta = itemStacks[i].getItemMeta();
                    meta.setDisplayName(Lang.get("van.menu.back", getTargetPlayer()));
                    itemStacks[i].setItemMeta(meta);

                    if (page == 0)
                        itemStacks[i].setDurability((short) 8);

                    continue;
                } else if (i == INVENTORY_SIZE - 5) {
                    itemStacks[i] = new ItemStack(Material.BARRIER, 1, (short) 0);
                    ItemMeta meta = itemStacks[i].getItemMeta();
                    meta.setDisplayName(Lang.get("van.menu.unequip", getTargetPlayer()));
                    itemStacks[i].setItemMeta(meta);
                    continue;
                } else if (i == INVENTORY_SIZE - 4) {
                    itemStacks[i] = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
                    ItemMeta meta = itemStacks[i].getItemMeta();
                    meta.setDisplayName(Lang.get("van.menu.forward", getTargetPlayer()));
                    itemStacks[i].setItemMeta(meta);

                    if (!(vanityItems.size() > (INVENTORY_SIZE * (page + 1))))
                        itemStacks[i].setDurability((short) 8);

                    continue;
                } else if (i == INVENTORY_SIZE - 9) {
                    itemStacks[i] = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
                    ItemMeta meta = itemStacks[i].getItemMeta();
                    meta.setDisplayName(Lang.get("van.menu.first", getTargetPlayer()));
                    itemStacks[i].setItemMeta(meta);

                    if (page == 0)
                        itemStacks[i].setDurability((short) 8);

                    continue;
                } else if (i == INVENTORY_SIZE - 1) {
                    itemStacks[i] = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
                    ItemMeta meta = itemStacks[i].getItemMeta();
                    meta.setDisplayName(Lang.get("van.menu.last", getTargetPlayer()));
                    itemStacks[i].setItemMeta(meta);

                    if (!(vanityItems.size() > (INVENTORY_SIZE * (page + 1))))
                        itemStacks[i].setDurability((short) 8);

                    continue;
                }

                itemStacks[i] = new ItemStack(Material.STAINED_GLASS_PANE, 1);
                ItemMeta meta = itemStacks[i].getItemMeta();
                meta.setDisplayName(" ");
                itemStacks[i].setItemMeta(meta);
            }

            if (inv != null) {
                Bukkit.getScheduler().runTask(Vanity.getInstance(), () -> {
                    inv.setContents(itemStacks);
                    getTargetPlayer().updateInventory();
                });
            }
        });
    }

    private void loadMenu(Player targetPlayer, List<InventoryItem> vanityItems, String category) {
        Bukkit.getScheduler().runTaskAsynchronously(Vanity.getInstance(), () -> {
            this.vanityItems = vanityItems;
            player = targetPlayer;
            this.catagory = category;

            for (int i = 0; i < itemStacks.length - 9; i++) {
                itemStacks[i] = null;
                inventoryItems[i] = null;
            }

            for (int i = 0; i < (vanityItems.size() > ((INVENTORY_SIZE * (page + 1)) - (9 * page)) ? ((INVENTORY_SIZE) - 9) : (vanityItems.size() - (INVENTORY_SIZE * page))); i++) {
                itemStacks[i] = vanityItems.get(i + ((INVENTORY_SIZE * page))).getItemStack(targetPlayer);
                inventoryItems[i] = vanityItems.get(i + ((INVENTORY_SIZE * page)));
            }

            if (inv != null) {
                Bukkit.getScheduler().runTask(Vanity.getInstance(), () -> {
                    inv.setContents(itemStacks);
                    targetPlayer.updateInventory();
                });
            }
        });
    }

    @Override
    public boolean execute(int location, int rawSlot) {
        if (location == rawSlot) {//Validate the click is in the top of the inventory

            if (inventoryItems[rawSlot] != null)
                ((ActionItemStack) inventoryItems[rawSlot]).getExecutor().executeAction(player, inventoryItems[rawSlot], null);

            // Pagination slots
            switch (location) {
                case INVENTORY_SIZE - 6: {
                    if (page == 0) {
                        Vanity.getInstance().getLogger().info("Page == 0");
                        return false;
                    }

                    page--;
                    this.updateButtons();
                    this.loadMenu(getTargetPlayer(), vanityItems, catagory);
                }
                break;
                case INVENTORY_SIZE - 5: {

                }
                break;
                case INVENTORY_SIZE - 4: {
                    if (((page + 1) * INVENTORY_SIZE) > vanityItems.size()) {
                        Vanity.getInstance().getLogger().info("Page final");
                        return false;
                    }

                    page++;
                    this.updateButtons();
                    this.loadMenu(getTargetPlayer(), vanityItems, catagory);
                }
                break;

                case INVENTORY_SIZE - 9: {
                    if (page == 0) {
                        Vanity.getInstance().getLogger().info("Page == 0");
                        return false;
                    }

                    page = 0;
                    this.updateButtons();
                    this.loadMenu(getTargetPlayer(), vanityItems, catagory);
                } break;

                case INVENTORY_SIZE - 1: {
                    if (page == 0) {
                        Vanity.getInstance().getLogger().info("Page == 0");
                        return false;
                    }

                    page = (INVENTORY_SIZE - 10) / vanityItems.size();
                    this.updateButtons();
                    this.loadMenu(getTargetPlayer(), vanityItems, catagory);
                } break;
            }

            return false;
        } else {
            return true;
        }
    }
}
