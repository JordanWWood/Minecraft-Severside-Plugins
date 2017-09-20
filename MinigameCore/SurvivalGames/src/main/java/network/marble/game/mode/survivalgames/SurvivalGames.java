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
            ts.minimumNumberOfPlayers = 6;
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
		return UUID.fromString("40b656ce-1a32-4ce8-a1bf-30082f4fd59a");
	}

	@Override
	public String getVersion() {
		return "1";
	}
}
