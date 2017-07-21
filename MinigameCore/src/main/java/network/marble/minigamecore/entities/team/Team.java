package network.marble.minigamecore.entities.team;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import lombok.Getter;
import network.marble.minigamecore.generator.NameGenerator;

import network.marble.minigamecore.entities.player.MiniGamePlayer;

public class Team {
	@Getter int minimumNumberOfPlayers = 1;
	@Getter int maximumNumberOfPlayers = 1;
	@Getter List<MiniGamePlayer> players;
	String teamIdentifier;
	@Getter boolean usePlayerNameAsTeamIdentifier = true;
	@Getter String teamPrefix = "";
	@Getter String teamSuffix = "";
	@Getter boolean friendlyFire = false;
	@Getter boolean canSeeFriendlyInvisibles = false;
	@Getter NameTagVisibility nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAM;
	@Getter int color = 0;
	@Getter TeamCollisionRule teamCollisionRule = TeamCollisionRule.NEVER;

	public Team(TeamSetup setup){
		minimumNumberOfPlayers = setup.minimumNumberOfPlayers;
		maximumNumberOfPlayers = setup.maximumNumberOfPlayers;
		players = new ArrayList<>();
		teamIdentifier = setup.generateRandomName ? NameGenerator.getRandomName() : setup.teamIdentifier;
		usePlayerNameAsTeamIdentifier = setup.usePlayerNameAsTeamIdentifier;
		teamPrefix = setup.teamPrefix;
		teamSuffix = setup.teamSuffix;
		friendlyFire = setup.friendlyFire;
		canSeeFriendlyInvisibles = setup.canSeeFriendlyInvisibles;
		nameTagVisibility = setup.nameTagVisibility;
		color = setup.color;
		teamCollisionRule = setup.teamCollisionRule;
	}
	
	public Team(String name, int minimumPlayers, int maximumPlayers)
	{
		teamIdentifier = name;
		minimumNumberOfPlayers = minimumPlayers;
		maximumNumberOfPlayers = maximumPlayers;
		players = new ArrayList<>();
	}

	public String getTeamIdentifier(){
		return usePlayerNameAsTeamIdentifier ? players.get(0).getPlayer().getDisplayName() : teamIdentifier;
	}

	public WrapperPlayServerScoreboardTeam getScoreboardTeamPacket() {
		WrapperPlayServerScoreboardTeam st = new WrapperPlayServerScoreboardTeam();
		st.setDisplayName(teamIdentifier);
		st.setPrefix(teamPrefix);
		st.setSuffix(teamSuffix);
		st.setNameTagVisibility(nameTagVisibility.getValue());
		st.setColor(color);
		st.setCollisionRule(teamCollisionRule.getValue());
		int data = 0;
		if (friendlyFire) data |= 1;
		if (canSeeFriendlyInvisibles) data |= 2;
		st.setPackOptionData(data);
		return st;
	}
}
