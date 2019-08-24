package network.marble.moderationslave.events;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * This event is called by moderationSlave asynchronously
 */
public class ModerationChatEvent extends Event implements Cancellable {
    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    @Getter @Setter private List<Player> recepients;
    @Getter @Setter private String prefix = null;
    @Getter @Setter private String suffix = null;
    @Getter @Setter private Player player;
    @Getter @Setter private String message;
    @Getter @Setter private List<TextComponent> nameHover;


    public ModerationChatEvent(List<Player> recipients, Player player, String message) {
        this.recepients = recipients;
        this.player = player;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
