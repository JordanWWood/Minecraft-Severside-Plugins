package network.marble.game.mode.survivalgames;

import network.marble.game.mode.survivalgames.listeners.StateListener;
import network.marble.minigamecore.entities.game.MiniGame;
import network.marble.minigamecore.entities.team.TeamSetup;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.UUID;

public class SurvivalGames implements MiniGame {

    public ArrayList<Listener> getEventListeners() {
        return new ArrayList<Listener>() {private static final long serialVersionUID = 1L;{add(new StateListener());}};
    }

    public String getName() {
        return "Survival Games";
    }

    public boolean canBeAborted() {
        return true;
    }

    public ArrayList<TeamSetup> getTeamSetups() {
        return new ArrayList<TeamSetup>() {
			private static final long serialVersionUID = 1L;
		{
            TeamSetup ts = new TeamSetup();
            ts.minimumNumberOfPlayers = 3;
            ts.maximumNumberOfPlayers = 24;
            ts.usePlayerNameAsTeamIdentifier = true;
            ts.areEnemyInvisibles = false;
            add(ts);
        }};
    }

    public boolean isNumberOfTeamsDynamic() {
        return false;
    }

    public int minimumNumberOfTeams() { return 12; }

    public int maximumNumberOfTeams() {
        return 24;
    }

    public int getLobbyCountDownTime() {
        return 15;
    }

	@Override
	public UUID getGameId() {
		return UUID.fromString("7a4605a5-4508-4277-b91b-495a38878b58");
	}

	@Override
	public String getVersion() {
		return "1";
	}
}
