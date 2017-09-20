package network.marble.minigamecore.entities.game;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.event.Listener;

import network.marble.minigamecore.entities.team.TeamSetup;

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
