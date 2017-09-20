package network.marble.minigamecore.entities.menus.quitgame;

import network.marble.inventoryapi.inventories.ConfirmationMenu;
import org.bukkit.entity.Player;

public class QuitGameMenu extends ConfirmationMenu {
    public QuitGameMenu(Player player) {
        super(player, null, new QuitGameExecutor(player), "Leave Game", "Cancel");
    }
}
