package network.marble.minigamecore.commands;

import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.CommandManager;
import network.marble.minigamecore.managers.PlayerExpectationManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length > 0) switch (args[0]) {
                case "expect":
                    if (args.length > 1) switch (args[1]) {
                        case "*":
                        case "all":
                        case "any":
                            PlayerExpectationManager.overrideExpected = true;
                            sender.sendMessage("Player expectation overridden");
                        break;

                        case "none":
                            PlayerExpectationManager.overrideExpected = false;
                            sender.sendMessage("Player expectation reinstated");
                            break;

                        default:
                            UUID id;
                            try {
                                id = UUID.fromString(args[1]);
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage("Failed to parse UUID from: "+args[1]);
                                break;
                            }
                            PlayerType playerType;
                            try {
                                playerType = args.length < 3 ? PlayerType.PLAYER : PlayerType.valueOf(args[2]);
                            } catch (IllegalArgumentException e) {
                                playerType = PlayerType.PLAYER;
                            }
                            PlayerExpectationManager.addPrePlayerRank(id, playerType);
                            sender.sendMessage("Player expectation added for: "+id.toString());
                        break;
                    } else {
                        sender.sendMessage("Not Enough Args");
                    }
                    break;
                case "cro"://Command console execution restriction override
                	CommandManager.restrictionOverride = !CommandManager.restrictionOverride;
                    sender.sendMessage("Restriction override set to " + CommandManager.restrictionOverride);
                	break;
                default:
                    sender.sendMessage("Sorry no command here by that name");
                break;
            }
        } else sender.sendMessage("you no cmd, get fucked shorty");
        return false;
    }
}
