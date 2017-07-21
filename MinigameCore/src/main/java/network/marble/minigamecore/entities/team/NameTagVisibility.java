package network.marble.minigamecore.entities.team;

public enum NameTagVisibility {
    ALWAYS("always"),
    HIDE_FOR_OTHER_TEAM("hideForOtherTeams"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam");

    private final String value;

    NameTagVisibility(String value) { this.value = value; }

    public String getValue() { return value; }
}
