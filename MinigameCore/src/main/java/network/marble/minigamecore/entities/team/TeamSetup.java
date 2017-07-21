package network.marble.minigamecore.entities.team;

public class TeamSetup {
	
	public int minimumNumberOfPlayers = 1;
	public int maximumNumberOfPlayers = 1;
	public String teamIdentifier;
	public boolean usePlayerNameAsTeamIdentifier = false;
	public boolean generateRandomName = true;
	public String teamPrefix = "";
	public String teamSuffix = "";
	public boolean friendlyFire = false;
	public boolean canSeeFriendlyInvisibles = false;
	public boolean areEnemyInvisibles = false;
	public boolean teamMemberNumbersDynamic = false;
	public NameTagVisibility nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAM;
	public int color = 0;
	public TeamCollisionRule teamCollisionRule = TeamCollisionRule.NEVER;
	
}
