package network.marble.game.mode.survivalgames.commands;

import network.marble.game.mode.survivalgames.listeners.GameListener;
import network.marble.game.mode.survivalgames.listeners.StateListener;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.player.PlayerType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Whisper extends MinigameCommand {
    public Whisper(String name) {
        super(name);
        this.CANBERUNBY.add(PlayerType.PLAYER);
        this.COMMANDALIASES = Arrays.asList("whisper", "w");
    }

    @Override
    public boolean commandExecution(CommandSender commandSender, String s, String[] args) {
        Player p = (Player) commandSender;
        String message = args[0];
		for(int i = 1; i < args.length; i++){
			message += " " + args[i];
		}
		
        GameListener.sendMessage(StateListener.getMapConfig().getChat().getWhisperDistance(), message, p.getDisplayName(), p.getLocation());
        
        return true;
    }
}
