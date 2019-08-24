package network.marble.quickqueue.listeners;

import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.api.QueueAPI;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class QueueListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerJoinEvent event) {//TODO get party and invite information from hermes is available
        Player player = event.getPlayer();
        final UUID playerId = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            Future<Party> p = PartyManager.getInstance().getUserParty(playerId);//Populates the ledParties list

            try {
                Party party = p.get(10, TimeUnit.SECONDS);
                //Set up party anti-expiration if need be
                if(party != null && party.getLeader().equals(playerId)){
                    if(party.getMembers().size() == 1) PartyManager.getInstance().disbandParty(playerId);
                    else PartyManager.getInstance().haltExpiration(party.getPartyID());
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        player.getInventory().setHeldItemSlot(4);//TODO shouldn't this be in Hubby?
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(PlayerQuitEvent event) {
        QueueAPI.markPlayerOffline(event.getPlayer());

        //Stop party anti-expiration timer
        final UUID playerId = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            Future<Party> p = PartyManager.getInstance().getUserParty(playerId);
            PartyManager.getInstance().clearPlayerCache(playerId);

            try {
                Party party = p.get(10, TimeUnit.SECONDS);
                if(party != null){
                    PartyManager.getInstance().resumeExpiration(party.getPartyID());
                    PartyManager.getInstance().clearPlayerCache(playerId);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }
}
