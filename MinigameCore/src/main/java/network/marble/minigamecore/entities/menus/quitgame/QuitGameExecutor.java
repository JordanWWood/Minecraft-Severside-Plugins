package network.marble.minigamecore.entities.menus.quitgame;

import org.bukkit.entity.Player;

import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.itemstacks.InventoryItem;

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

