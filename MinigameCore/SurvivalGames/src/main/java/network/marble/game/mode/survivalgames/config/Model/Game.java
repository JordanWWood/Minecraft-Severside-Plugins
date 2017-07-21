package network.marble.game.mode.survivalgames.config.Model;

import lombok.Getter;

public class Game {
    @Getter DeathMatch deathMatch;
    @Getter Misc chests;
}
class Misc {
    @Getter float chestAmountFilled;
}