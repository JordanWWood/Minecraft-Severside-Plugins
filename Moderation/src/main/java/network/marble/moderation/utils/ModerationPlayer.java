package network.marble.moderation.utils;

import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;

public class ModerationPlayer {
    public ModerationPlayer(User u, Rank r) {
        this.u = u;
        this.r = r;
    }

    @Getter @Setter private User u;
    @Getter @Setter private Rank r;
}
