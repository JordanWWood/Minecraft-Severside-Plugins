package network.marble.minigamecore.entities.team;

public enum TeamCollisionRule {
    ALWAYS("always"),
    OTHER_TEAM("pushOtherTeams"),
    OWN_TEAM("pushOwnTeam"),
    NEVER("never");

    private final String value;

    TeamCollisionRule(String value) { this.value = value; }

    public String getValue() { return value; }
}
