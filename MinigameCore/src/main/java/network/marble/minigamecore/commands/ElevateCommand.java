package network.marble.minigamecore.commands;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messagelibrary.api.Lang;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

/**
 * Allows elevations from Moderator player type to Administrator
 */
public class ElevateCommand extends MinigameCommand {
    public ElevateCommand() {
        super("elevate");
        this.commandAliases = Collections.singletonList("elevate");
        this.canBeRunBy = Collections.singletonList(PlayerType.MODERATOR);
        this.setDescription("Elevate to Administrator Mode");
    }

    @Override
    public boolean commandExecution(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("This command can only be sent from in-game.");
            return true;
        }
        Player playerSender = (Player)sender;
        try {
            if (new User().getByUUID(playerSender.getUniqueId()).hasPermission("moderation.elevate")) {
                PlayerManager.unregisterPlayer(playerSender);
                PlayerManager.registerPlayer(playerSender, PlayerType.ADMINISTRATOR);
                playerSender.sendMessage(Lang.get("command.elevate.success", playerSender));
            } else {
                playerSender.sendMessage(Lang.get("global.permission.denied", playerSender));
            }
        } catch(APIException exc){
            playerSender.sendMessage(Lang.get("global.error", playerSender));
            exc.printStackTrace();
        }
        return true;
    }
}
