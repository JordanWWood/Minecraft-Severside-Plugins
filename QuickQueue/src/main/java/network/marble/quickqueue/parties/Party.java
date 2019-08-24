package network.marble.quickqueue.parties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.messagelibrary.api.MessageType;
import network.marble.quickqueue.QuickQueue;

public class Party {
    @Getter private UUID partyID;
    @Getter
    @Setter private UUID leader;
    private ArrayList<UUID> members;
    @Getter
    @Setter private volatile boolean isQueued;
    @Getter
    @Setter private volatile boolean memberInvitingEnabled;
    @Getter
    @Setter private GameMode queuedGame = null;
    @Getter
    @Setter private UUID queuedGameUUID;

    public Party(UUID partyID) {
        this(partyID, null, new ArrayList<>(), false, false);
    }

    public Party(UUID partyID, UUID leader) {
        this(partyID, leader, new ArrayList<>(), false, false);
    }

    public Party(UUID partyID, UUID leader, ArrayList<UUID> members, boolean isQueued, boolean memberInvitingEnabled) {
        this.partyID = partyID;
        this.leader = leader;
        this.members = members;
        this.isQueued = isQueued;
        this.memberInvitingEnabled = memberInvitingEnabled;
    }

    @Synchronized
    public void addMember(UUID member) {
        members.add(member);
    }

    @Synchronized
    public void removeMember(UUID member) {
        members.remove(member);
    }

    @Synchronized
    public Collection<UUID> getMembers(){
        return Collections.unmodifiableCollection(members);
    }

    @Synchronized
    public Collection<UUID> getMembersWithLeader(){
        List<UUID> all = new ArrayList<>();
        all.addAll(members);
        all.add(leader);
        return Collections.unmodifiableCollection(all);
    }
}
