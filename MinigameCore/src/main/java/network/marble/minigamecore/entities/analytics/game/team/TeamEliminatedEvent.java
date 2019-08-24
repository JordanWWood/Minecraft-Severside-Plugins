package network.marble.minigamecore.entities.analytics.game.team;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeamEliminatedEvent extends TeamEvent {

    public TeamEliminatedEvent(String teamName) {
        super(teamName, "ELIMINATED", "default");
    }

    public TeamEliminatedEvent(String teamName, String details) {
        super(teamName, "ELIMINATED", details);
    }
}
