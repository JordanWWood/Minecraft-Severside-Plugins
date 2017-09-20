package network.marble.game.mode.survivalgames.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;
import network.marble.game.mode.survivalgames.config.Model.*;
import network.marble.game.mode.survivalgames.listeners.StateListener;

public class MapConfig {
    @Getter Info info;
    @Getter Mobs mobs;
    @Getter Sponsor sponsor;
    @Getter Nature nature;
    @Getter Chat chat;
    @Getter Game game;

    @Getter Spawns spawns;

    @Getter Chests chests;

    public Location getPodiumLocation(int number){
    	return new Location(Bukkit.getWorld(StateListener.getCurrentGameWorld()),
                spawns.getPrimary().get(number).getX(),
                spawns.getPrimary().get(number).getY(),
                spawns.getPrimary().get(number).getZ(),
                spawns.getPrimary().get(number).getYaw(),
                spawns.getPrimary().get(number).getPitch()
        );
    }
}
