package network.marble.moderationslave.commands;

import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.utils.FontFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Report implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(ModerationSlave.getInstance(), () -> {
            if (sender instanceof Player) {
                if (args.length > 1 || args.length < 2) {
                    ((Player) sender).sendMessage(FontFormat.translateString("&cIncorrect arguments, you can use [Tab] to complete the reason. /report (Player) [Reason]"));
                    return;
                }

                User argUser;
                try {
                    argUser = new User().getByUsername(args[0]);

                    if (argUser == null) {
                        sender.sendMessage(FontFormat.translateString("&cUser does not exist"));
                    }
                } catch (APIException e) {
                    e.printStackTrace();
                }

                if (asTypeEnum(args[1]) == null) {
                    StringBuilder builder = new StringBuilder();
                    for (Type t : Type.values()) {
                        builder.append(" " + t.name());
                    }

                    sender.sendMessage(FontFormat.translateString("&c" + args[1] + " is not a valid reason. Try " + builder + "."));
                }

                if (args.length == 2) {
                    //TODO log to database

                    return;
                }

                //TODO invoke menu
            }
        });

        return false;
    }

    private Type asTypeEnum(String str) {
        for (Type t : Type.values()) {
            if (Objects.equals(t.name().toUpperCase(), str.toUpperCase())) {
                return t;
            }
        }

        return null;
    }


    enum Type {
        // TODO change materials
        HACKING("Hacking", new ItemStack(Material.REDSTONE_BLOCK)),
        SPAMMING("Spamming", new ItemStack(Material.BOW)),
        LANGUAGE("Language", new ItemStack(Material.NAME_TAG)),
        HARASSMENT("Harassment", new ItemStack(Material.RAW_FISH)),
        ADVERTISING("Advertising", new ItemStack(Material.SIGN)),
        TEAMING("Teaming", new ItemStack(Material.IRON_SWORD)),
        SKIN("Skin", new ItemStack(Material.SKULL)),
        TROLLING("Trolling", new ItemStack(Material.PUMPKIN)),
        IMPERSONATION("Impersonation", new ItemStack(Material.HAY_BLOCK));

        @Getter private String name;
        @Getter private ItemStack item;

        private Type(String name, ItemStack item) {
            this.name = name;
        }
    }
}
