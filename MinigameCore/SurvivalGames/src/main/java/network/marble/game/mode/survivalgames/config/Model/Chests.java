package network.marble.game.mode.survivalgames.config.Model;

import lombok.Getter;

import java.util.List;

public class Chests {
    @Getter List<List<TierItem>> tiers;
    @Getter List<Locations> locations;
}
