package network.marble.minigamecore.entities.analytics.game.team;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeamCreateEvent extends TeamEvent {

    public TeamCreateEvent(String teamName) {
        super(teamName, "CREATE", "default");
    }

    public TeamCreateEvent(String teamName, String details) {
        super(teamName, "CREATE", details);
    }
}
