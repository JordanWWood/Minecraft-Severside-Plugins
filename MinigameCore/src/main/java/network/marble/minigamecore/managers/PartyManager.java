package network.marble.minigamecore.managers;

import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.Party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyManager {
    private static PartyManager instance;

    private static List<Party> parties = new ArrayList<Party>();

    public boolean isInParty(MiniGamePlayer player) {
        return isInParty(player.id);
    }

    public boolean isInParty(UUID id) {
        if (parties.size() <= 0) return false;
        return getPartyByPlayers(id) != null;
    }

    public Party getPartyByPlayers(MiniGamePlayer player) {
        return getPartyByPlayers(player.id);
    }

    public Party getPartyByPlayers(UUID id) {
        if (parties.size() > 0) for (Party party: parties) {
            if (party.leader.equals(id)) return party;
            else for(UUID player : party.players) {
                if (player.equals(id)) return party;
            }
        }
        return null;
    }

    public static PartyManager getInstance() {
        if (instance == null) instance = new PartyManager();
        return instance;
    }
}
