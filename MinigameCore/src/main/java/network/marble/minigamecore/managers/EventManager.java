package network.marble.minigamecore.managers;

import network.marble.minigamecore.MiniGameCore;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private static List<Listener> stack = new ArrayList<>();

    public void registerEvent(Listener listener) {
        registerEvent(listener, false);
    }

    public void registerEvent(Listener listener, boolean ignoreStack) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, MiniGameCore.instance);
        if (!ignoreStack) stack.add(listener);
    }

    public void registerEvents(List<Listener> listeners) {
        listeners.forEach(this::registerEvent);
    }

    public void registerEvents(List<Listener> listeners, boolean ignoreStack) {
        listeners.forEach(listener -> registerEvent(listener, ignoreStack));
    }

    public void unregisterEvent(Listener listener) {
        if (stack.contains(listener)) stack.remove(listener);
        HandlerList.unregisterAll(listener);
    }

    public void unregisterEvents(List<Listener> listeners) {
        listeners.forEach(this::unregisterEvent);
    }

    public void clearStack() {
        stack.forEach(HandlerList::unregisterAll);
        stack.clear();
    }

    public static EventManager getInstance() {
        if (instance == null) instance = new EventManager();
        return instance;
    }
}
