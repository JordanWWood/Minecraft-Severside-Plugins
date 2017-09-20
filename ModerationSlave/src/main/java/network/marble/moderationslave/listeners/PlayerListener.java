package network.marble.moderationslave.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import network.marble.dataaccesslayer.models.plugins.moderation.Case;
import network.marble.dataaccesslayer.models.plugins.moderation.CaseOutcome;
import network.marble.moderationslave.ModerationSlave;
import network.marble.moderationslave.commands.cmdPunishment;
import network.marble.moderationslave.events.ModerationChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.moderationslave.utils.FontFormat;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private HashMap<UUID, LinkedList<String>> messageLog = new HashMap<>();
    public static List<String> regexStrings = Collections.synchronizedList(new ArrayList<String>());

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onModerationChat(ModerationChatEvent e) {
        if(!e.isCancelled()){
            try {
                User u = new User().getByUUID(e.getPlayer().getUniqueId());
                List<Case> cases = new Case().getActiveByJudgementeeOutcome(u.getId(), CaseOutcome.Muted);

                if (cases != null && cases.isEmpty()) {
                    Rank r = new Rank().get(u.getRank_id());

                    String message = e.getMessage();

                    ArrayList<BaseComponent> components = new ArrayList<>();
                    components.add(new TextComponent(FontFormat.translateString("Rank: " + r.prefix + r.name)));
                    components.add(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
                    components.add(new TextComponent(FontFormat.translateString("Player Since: Beta")));//TODO calculate and render in format: April, 2016
                    BaseComponent[] hoverToSend = (BaseComponent[]) components.toArray(new BaseComponent[components.size()]);

                    TextComponent parentObject = new TextComponent("");
                    TextComponent prefixTC = new TextComponent(FontFormat.translateString(r.getPrefix() + u.getDisplayName() + r.getSuffix() + " \u00BB"));
                    prefixTC.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverToSend));
                    parentObject.addExtra(prefixTC);
                    parentObject.addExtra(new TextComponent(TextComponent.fromLegacyText(FontFormat.translateString(r.getSuffix() + " " + message))));


                    for (Player p : e.getRecepients())
                        p.spigot().sendMessage(parentObject);
                } else {
                    e.getPlayer().sendMessage(FontFormat.translateString("&cYou have been muted!"));
                }
            } catch (APIException e1) {
                e1.printStackTrace();
            }//TODO swap when sideloading works
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
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

        Bukkit.getServer().getPluginManager().callEvent(new ModerationChatEvent(new ArrayList<Player>(Bukkit.getOnlinePlayers()), e.getPlayer(), e.getMessage()));
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(ModerationSlave.getInstance(), () -> {
            new Thread(() -> {
                User u = null;
                List<Case> cases = null;
                try {
                    u = new User().getByUUID(e.getPlayer().getUniqueId());
                    cases = new Case().getOpenByJudgementee(u.getId());
                } catch (APIException ex) {
                    ex.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                Case c = null;
                for (Case ca : cases) {
                    if (ca.isPardoned()) continue;
                    if (ca.getJudgement_session_id() != null) {
                        c = ca;
                        break;
                    }
                }

                if (c != null) {
                    cmdPunishment.limboPlayer(e.getPlayer(), null);
                }
            }).start();
        }, 10);

    }
}






















