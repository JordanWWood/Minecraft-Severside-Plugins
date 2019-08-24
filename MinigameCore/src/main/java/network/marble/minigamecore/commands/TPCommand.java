package network.marble.minigamecore.commands;

import network.marble.messagelibrary.api.Lang;
import network.marble.messagelibrary.api.MessageLibrary;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Allows spectators, moderators and administrators to teleport to players
 */
public class TPCommand extends MinigameCommand {

    public TPCommand() {
        super("tp");
        this.commandAliases = Collections.singletonList("teleport");
        this.canBeRunBy = Arrays.asList(PlayerType.ADMINISTRATOR, PlayerType.MODERATOR, PlayerType.SPECTATOR);
        this.setDescription("Teleport to players");
    }

    @Override
    public List<String> tabCompletion(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> returns = Collections.emptyList();
        for(MiniGamePlayer plr : PlayerManager.getPlayers(PlayerType.PLAYER)){
            returns.add(plr.getPlayer().getName());
        }
        return returns;
    }

    @Override
    public boolean commandExecution(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("This command can only be sent from ingame.");
            return true;
        }
        Player playerSender = (Player)sender;
        if(args.length < 1){
            sender.sendMessage(Lang.get("command.tp.failure.args", playerSender));
            return true;
        }
        String stringTarget = args[0];
        Player playerTarget = null;
        for(MiniGamePlayer plr : PlayerManager.getPlayers(PlayerType.PLAYER)){
            if(plr.getPlayer().getName().toLowerCase().startsWith(stringTarget.toLowerCase())) {
                playerTarget = plr.getPlayer();
            }
        }
        if(playerTarget == null){
            playerSender.sendMessage(Lang.get("command.tp.failure.notfound", playerSender));
            return true;
        }
        playerSender.teleport(playerTarget.getLocation());
        String line = Lang.get("command.tp.success", playerSender);
        line = Lang.replaceUnparsedTag(line,"target.name", MessageLibrary.getDisplayName(playerTarget.getUniqueId()));
        playerSender.sendMessage(line);
        return true;
    }
}
