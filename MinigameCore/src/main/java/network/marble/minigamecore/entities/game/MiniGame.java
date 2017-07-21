package network.marble.minigamecore.entities.game;

import java.util.ArrayList;
import java.util.UUID;

import network.marble.minigamecore.entities.command.MinigameCommand;
import network.marble.minigamecore.entities.team.TeamSetup;
import org.bukkit.event.Listener;

public interface MiniGame {

	// Game Setup
    ArrayList<Listener> getEventListeners();

	String getName();

	String getVersion();

	UUID getGameId();

	ArrayList<TeamSetup> getTeamSetups();

	/**
	 * If set to true, the team will be setup using the first TeamSetup provided in {link teamSetups}
	 *
	 */
    boolean isNumberOfTeamsDynamic();
}
