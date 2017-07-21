package network.marble.game.mode.survivalgames.config.Model;

import lombok.Getter;

import java.util.List;

public class Spawns {
    @Getter List<Spawn> primary;
    @Getter List<Spawn> deathmatch;
}
