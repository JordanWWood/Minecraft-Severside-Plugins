package network.marble.game.mode.survivalgames.config.Model;

import org.bukkit.util.Vector;

import lombok.Getter;

public class DeathMatch {
    @Getter int minPlayers;
    @Getter Boolean shrinking = true;
    @Getter int radius = 40;
    @Getter Vector center = new Vector(0,0,0);
}
