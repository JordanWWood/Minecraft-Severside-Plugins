package network.marble.minigamecore.entities.player;

public enum PlayerType {
    SPECTATOR(0),
    PLAYER(1),
    MODERATOR(2),
    ADMINISTRATOR(3);

    public static PlayerType get(int code) {
        switch(code) {
            case  0: return SPECTATOR;
            case  1: return PLAYER;
            case  2: return MODERATOR;
            case  3: return ADMINISTRATOR;
        }
        return null;
    }

    int id;

    PlayerType(int val){
        id = val;
    }
}