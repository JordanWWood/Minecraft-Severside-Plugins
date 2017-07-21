package network.marble.minigamecore.entities.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    public List<UUID> players = new ArrayList<UUID>();
    public UUID leader;
}
