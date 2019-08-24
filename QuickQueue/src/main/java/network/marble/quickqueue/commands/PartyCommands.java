package network.marble.quickqueue.commands;

import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.actions.AcceptInvite;
import network.marble.quickqueue.actions.CreateParty;
import network.marble.quickqueue.actions.DisbandParty;
import network.marble.quickqueue.actions.InvitePlayer;
import network.marble.quickqueue.actions.LeaveParty;
import network.marble.quickqueue.actions.LeaveQueue;
import network.marble.quickqueue.actions.RemoveMember;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommands implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length == 1){
                switch(args[0]){
                    case "create":
                        CreateParty.getInstance().executeAction(player, null, null);
                        break;
                    case "disband":
                        DisbandParty.getInstance().executeAction(player, null, null);
                        break;
                    case "leave":
                    case "leaveparty":
                        LeaveParty.getInstance().executeAction(player, null, new String[0]);
                        break;
                    case "exit":
                    case "exitqueue":
                        LeaveQueue.getInstance().executeAction(player, null, null);
                        break;
                }
            }
            if(args.length > 1) {
                switch (args[0]) {
                    case "invite":
                        if (args.length == 2) {
                            InvitePlayer.getInstance().executeAction(player, null, new String[]{args[1]});
                        } else {
                            Lang.chat("qq.info.command.invite.usage", player);
                        }
                        break;
                    case "accept":
                    case "join":
                        if (args.length == 2) {
                            AcceptInvite.getInstance().executeAction(player, null, new String[]{args[1]});
                        } else {
                            Lang.chat("qq.info.command.join.usage", player);
                        }
                        break;
                    case "remove":
                    case "removeplayer":
                        if (args.length == 2) {
                            RemoveMember.getInstance().executeAction(player, null, new String[]{args[1]});
                        } else {
                            Lang.chat("qq.info.command.remove.usage", player);
                        }
                        break;
                }
            } else {
                String[] lines = Lang.get("qq.info.command.usage", player).split("\\\\n");
                for(String line : lines) player.sendMessage(line);
            }
        }
        return false;
    }
}
