package network.marble.minigamecore.entities.command;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.CommandManager;
import network.marble.minigamecore.managers.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MinigameCommand extends BukkitCommand {
    public List<PlayerType> canBeRunBy = new ArrayList<>();
    public List<String> commandAliases = new ArrayList<>();

    /***
     * Construct a new command.
     * @param name What the user of the command will type (without the leading forward slash)
     */
    public MinigameCommand(String name){
        super(name);
        commandAliases = Collections.singletonList(name);
    }

    /**
     * @deprecated Please use {@link #commandExecution(CommandSender, String, String[])}
     */
    @Override
    @Deprecated
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender == null) {
            MiniGameCore.logger.severe("Command sender is null");
            return false;
        }
        if (sender instanceof Player) {
            MiniGamePlayer mp = PlayerManager.getPlayer(((Player) sender));
            if (mp == null || mp.playerType == null) sender.sendMessage("Something has gone wrong, if this keeps happening please contact a member of staff");
            else if (canBeRunBy.contains(mp.playerType)) return commandExecution(sender, label, args);
            else sender.sendMessage("Unknown command!");
        } else if(CommandManager.restrictionOverride) return commandExecution(sender, label, args);
        else sender.sendMessage("Unknown command!");
        return false;
    }

    /***
     * Method that runs when a registered command corresponding to it is executed.
     * @param sender The CommandSender that executed the command
     * @param label
     * @param args Arguments (space-separated) following the command keyword
     * @return Whether the command was successfully executed.
     */
    public abstract boolean commandExecution(CommandSender sender, String label, String[] args);

    @Override
    @Deprecated
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return commandAliases.contains(alias) ? tabCompletion(sender, alias, args): super.tabComplete(sender, alias, args);
    }

    public List<String> tabCompletion(CommandSender sender, String alias, String[] args) {
        return super.tabComplete(sender, alias, args);
    }
}
