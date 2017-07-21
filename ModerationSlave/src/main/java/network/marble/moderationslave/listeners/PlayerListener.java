package network.marble.moderationslave.listeners;

import network.marble.moderationslave.utils.FontFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerListener implements Listener {
    private HashMap<UUID, LinkedList<String>> messageLog = new HashMap<>();
    private List<String> regexStrings = new ArrayList<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        // Chat Filter
        for (String s : regexStrings) {
            Pattern pattern = Pattern.compile(s);
            Matcher m = pattern.matcher(e.getMessage());

            if (m.matches()) {
                e.setCancelled(true);
                p.sendMessage(FontFormat.translateString("&cYou cannot say that!"));
            }
        }

        // Message log
        if (!messageLog.containsKey(p.getUniqueId())) messageLog.put(p.getUniqueId(), new LinkedList<>());

        LinkedList<String> messages = messageLog.get(p.getUniqueId());
        if (messages.size() >= 10) messages.removeFirst();

        messages.add(e.getMessage());
        messageLog.replace(p.getUniqueId(), messages);
    }
}
