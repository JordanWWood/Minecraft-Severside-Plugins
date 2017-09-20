package network.marble.minigamecore.entities.menus.quitgame;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.inventories.ConfirmationMenu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import org.bukkit.entity.Player;

public class QuitGameExecutor implements ActionExecutor {
    private final Player player;

    public QuitGameExecutor(Player player) {
        this.player = player;
    }

    @Override
    public void executeAction(Player triggeringPlayer, InventoryItem itemTriggered, String[] args) {
        player.kickPlayer("Hub Command");
    }
}

