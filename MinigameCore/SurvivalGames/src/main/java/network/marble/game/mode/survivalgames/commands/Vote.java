package network.marble.game.mode.survivalgames.commands;

import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.game.mode.survivalgames.listeners.StateListener;
import network.marble.game.mode.survivalgames.managers.VoteManager;
import network.marble.game.mode.survivalgames.utils.FontFormat;
import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.player.PlayerType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Vote extends MinigameCommand {
    private static Map<UUID, Integer> voted = new HashMap<>();
    public boolean canVote = true;

    public Vote(String name) {
        super(name);
        this.CANBERUNBY.add(PlayerType.PLAYER);
        this.COMMANDALIASES = Arrays.asList("vote", "v");
    }

    @Override
    public boolean commandExecution(CommandSender commandSender, String s, String[] args) {
        Player p = (Player) commandSender;

        if (canVote = false) {
            commandSender.sendMessage(FontFormat.translateString(StateListener.getPrefix() + " &cThe map has already been selected"));
        }
        if (args.length > 0 && args.length < 2) {
	        if (!voted.containsKey(p.getUniqueId())) {
                int vote = Integer.valueOf(args[0]) - 1;//Minus the aesthetic one base
                voted.put(p.getUniqueId(), vote);

                new Thread(() -> {
                    try {
                        User u = new User().getByUUID(p.getUniqueId());//TODO change when sideloading exists
                        Rank r = new Rank().get(u.getRank_id());
                        
                        VoteManager.getInstance().addVote(vote, r, p);
                    } catch (Exception e) {
                        e.printStackTrace();
                        VoteManager.getInstance().addVote(vote, null, p);
                    }
                }).start();
	        } else {
	        	if(!voted.get(p.getUniqueId()).equals(Integer.valueOf(args[0]) - 1)){
		        	int oldVote = voted.get(p.getUniqueId());
		        	new Thread(() -> {
	                    try {
	                        User u = new User().getByUUID(p.getUniqueId());//TODO change when sideloading exists
	                        Rank r = new Rank().get(u.getRank_id());
	
	                        VoteManager.getInstance().removeVote(oldVote, r);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        VoteManager.getInstance().removeVote(oldVote, null);
	                    }
	                }).start();
		        	
		        	int vote = Integer.valueOf(args[0]) - 1;//Minus the aesthetic one base
	                voted.put(p.getUniqueId(), vote);
	                
	                new Thread(() -> {
	                    try {
	                        User u = new User().getByUUID(p.getUniqueId());//TODO change when sideloading exists
	                        Rank r = new Rank().get(u.getRank_id());
	
	                        VoteManager.getInstance().addVote(vote, r, p);
	                    } catch (Exception e) {
	                    	VoteManager.getInstance().addVote(vote, null, p);
	                        e.printStackTrace();
	                    }
	                }).start();
	        	}else{
	        		VoteManager.getInstance().sendVoteMessage(p);
	        	}
	        }
        } else {
        	VoteManager.getInstance().sendVoteMessage(p);
            p.sendMessage(FontFormat.translateString(StateListener.getPrefix()+" &2Vote using /v [map number]"));
        }
        return false;
    }
}
