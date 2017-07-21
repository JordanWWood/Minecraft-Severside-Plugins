package network.marble.inventoryapi.interfaces;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface UUIDSearchResult {

	public abstract ArrayList<UUID> getUUIDs(Player sourcePlayer);
	
}
