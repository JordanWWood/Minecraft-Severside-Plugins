package network.marble.minigamecore.entities.game;

public enum GameStatus {
    INITIALIZING, //game initialising
    LOBBYING, //players joining and waiting in lobby
    PRESTART, //all players joined, teams been decided
    INGAME, //game running
    FINISHED, //on game completion
    ENDED //once game has clean up, booted all plays etc
}
