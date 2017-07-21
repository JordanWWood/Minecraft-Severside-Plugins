package network.marble.dataaccesslayer.models.user;


import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.bukkit.DataAccessLayer;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.base.BaseModel;
import network.marble.dataaccesslayer.models.plugins.friends.Friend;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import okhttp3.Request;

import java.util.*;

public class User extends BaseModel<User> {
    public User() {
        super("users", "users", "user");
    }

    @Getter @Setter
    public String name; //Lowercase username
    
    @Getter @Setter
    public String displayName; //Username with preserved capitalisation

    @Getter @Setter
    public UUID rank_id;

    @Getter @Setter
    public Moderation moderation;

    @Getter @Setter
    public String ip;

    @Getter @Setter
    public Map<String,String> preferences = new HashMap<>();
    
    @Getter @Setter
    public List<BlockedUser> blockedUsers = new ArrayList<>();

    @Getter @Setter
    public List<UserBadge> badges = new ArrayList<>();

    @Getter @Setter
    public UUID uuid;

    @Getter @Setter
    public ForumData forumdata;

    @Getter @Setter
    public Map<UUID,Long> balances = new HashMap<>();
    
    @Getter @Setter
    public boolean inJudgement;

    @Getter @Setter
    public Map<UUID,Integer> vanityitems = new HashMap<>();

    @Getter @Setter
    public Map<Integer, UUID> equippedVanityItems = new HashMap<>();

    @Getter @Setter
    public String language;

    public User getByUUID(UUID uuid) throws APIException {
        return getSingle(urlEndPoint+"/uuid/"+uuid.toString());
    }

    /***
     * Get a user model of a player by their username
     * @param username Converted to lowercase for searching internally
     * @return User model of the user with the entered username or null if no user is found
     * @throws APIException
     */
    public User getByUsername(String username) throws APIException {
        return getSingle(urlEndPoint+"/name/"+username.toLowerCase());
    }

    public User getByForumId(int forumId) throws APIException {
        return getSingle(urlEndPoint+"/forumid/"+forumId);
    }

    public List<Friend> getFriendsOf() {
        try {
            return new Friend().getFriendsOf(this.getUuid());
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFriendOf(UUID uuid) {
        User u = null;
        List<Friend> friends = null;
        try {
            friends = this.getFriendsOf();
            u = new User().getByUUID(uuid);
        } catch (APIException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < friends.size(); i++) {
            return u.getUuid().equals(friends.get(i).getSender()) || u.getUuid().equals(friends.get(i).getReceiver());
        }
        return false;
    }

    public boolean hasPermission(String permissionKey) {
        if (!this.exists() || this.rank_id == null) return false;
        Rank rank;
        try {
            rank = new Rank().getFull(this.rank_id);
        } catch (APIException e) {
            DataAccessLayer.getInstance().logger.warning("Failed to find user rank model");
            e.printStackTrace();
            return false;
        }

        for (String permission : rank.getPermissions()) {
            if (permission.equals("*")) return true;
            if (permission.contains("*")) {
                String[] checks = permission.split("/\\*/");
                if (checks.length > 0 && permissionKey.startsWith(checks[0])) return true;
            } else {
                if (permission.equals(permissionKey)) return true;
            }
        }
        return false;
    }

    @Override
    public Class<?> getTypeClass() {
        return User.class;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", rank_id=" + rank_id +
                ", moderation=" + moderation +
                ", ip='" + ip + '\'' +
                ", preferences=" + preferences +
                ", blockedUsers=" + blockedUsers +
                ", badges=" + badges +
                ", uuid=" + uuid +
                ", forumdata=" + forumdata +
                ", balances=" + balances +
                ", inJudgement=" + inJudgement +
                ", vanityitems=" + vanityitems +
                ", equippedVanityItems=" + equippedVanityItems +
                ", language='" + language + '\'' +
                "} " + super.toString();
    }
}
