package network.marble.quickqueue.managers;

import com.github.jedis.lock.JedisLock;
import com.google.gson.Gson;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.RedisManager;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.dataaccesslayer.models.plugins.moderation.Rank;
import network.marble.dataaccesslayer.models.user.User;
import network.marble.messageapi.api.MessageAPI;
import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.messages.AtlasDequeueParty;
import network.marble.quickqueue.messages.AtlasQueueParty;
import network.marble.quickqueue.messages.GameJoinData;
import network.marble.quickqueue.messages.GameLeaveData;
import network.marble.quickqueue.messages.Message;
import network.marble.quickqueue.parties.Invite;
import network.marble.quickqueue.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//TODO messages
//TODO inventories
//TODO updaters
/**
 * Logic for management of parties
 */
public class PartyManager {
    private static final int EXPIRATION_TIME = 600;
    private static final String LOCK_PREFIX = "lock_";
    private static final String PARTY_PREFIX = "party.";
    private static final String MEMBER_PREFIX = "partymember.";
    private static final String INVITE_PREFIX = "invite.";

    private static PartyManager instance;
    private static final Gson g = new Gson();

    /**
     * Gets an instance of the PartyManager, creates one if one does not exist
     */
    public static PartyManager getInstance(){
        if(instance == null) instance = new PartyManager();
        return instance;
    }

    /**
     * Returns a {@link Party Party}
     * @param user The user to get the party of
     * @return The party the user is a member of
     */
    public CompletableFuture<Party> getUserParty(final UUID user){
        //Future to return;
        CompletableFuture<Party> futureParty = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                futureParty.complete(getUserPartySync(user));
            } catch (Exception e) {
                futureParty.completeExceptionally(e);
            }
        });

        return futureParty;
        //Lang.chat("global.error", player);
    }

    /**
     * Returns a {@link Party Party}
     * @param user The user to get the party of
     * @return The party the user is a member of
     */
    public Party getUserPartySync(final UUID user) throws Exception{
        //Future to return;
        JedisLock lock = null;
        try(Jedis jedis = RedisManager.getPool().getResource()){
            //Get user party id
            UUID partyId = RedisManager.getUUID(jedis, MEMBER_PREFIX + user);
            Party p = null;

            if(partyId != null) {
                //Lock their party in redis and get it
                lock = new JedisLock(jedis, LOCK_PREFIX + PARTY_PREFIX + user, 3000, 10000);
                lock.acquire();
                String rawParty = jedis.get(PARTY_PREFIX + partyId.toString());
                if(rawParty != null){
                    p = g.fromJson(rawParty, Party.class);
                    //Update partyCacheByMember cache
                    if(p != null){
                        //Update partyCacheByMember list
                        updateCaches(p, user);
                    }
                }
            }
            return p;
        }catch (Exception e){
            throw e;
        }finally {
            RedisManager.releaseLock(lock);
        }
    }

    /**
     * Creates a new party
     * @param leader The player who will lead this party
     * @return Whether the operation was successful
     */
    public Future<PartyResult> createParty(final UUID leader){
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> createPartySync(leader, result));
        return result;
    }

    private void createPartySync(final UUID leader, final CompletableFuture<PartyResult> result){
        if(partyCacheByMember.contains(leader)){
            if(result != null) result.complete(new PartyResult(false, "qq.error.already.in.party"));
        }else{
            JedisLock lock = null;
            Transaction t = null;
            try(Jedis jedis = RedisManager.getPool().getResource()){
                //Check the player isn't already in a party
                if (!jedis.exists(MEMBER_PREFIX + leader)) {
                    //Set up new party
                    UUID partyId = UUID.randomUUID();
                    Party p = new Party(partyId, leader);

                    //Lock member object
                    lock = new JedisLock(jedis, LOCK_PREFIX + MEMBER_PREFIX + leader, 3000, 10000);
                    t = jedis.multi();
                    //Party creation transaction
                    t.set(PARTY_PREFIX + partyId, g.toJson(p));
                    t.expire(PARTY_PREFIX + partyId, EXPIRATION_TIME);
                    setPlayerPartySync(t, leader, partyId, true);

                    updateCaches(p, leader);
                    haltExpiration(partyId);
                    if(result != null) result.complete(new PartyResult(true, "qq.success.create"));
                }else{//Fail if they are in a party
                    if(result != null) result.complete(new PartyResult(false, "qq.error.already.in.party"));
                }
            }catch (Exception e){
                e.printStackTrace();
                if(result != null) result.complete(new PartyResult(false, "qq.error.create"));
            }finally {
                RedisManager.releaseLock(lock);
                if(t != null) t.clear();
            }
        }
    }

    /**
     * Transactionally adds a player to a party. The player MUST be locked when using this method.
     * @param transaction The transaction to use
     * @param player The UUID of the player who is having their party set
     * @param party the ID of the party the player is joining
     * @param execute Whether to call exec on the transaction or not
     */
    private void setPlayerPartySync(final Transaction transaction, final UUID player, final UUID party, final boolean execute){
        transaction.set(MEMBER_PREFIX + player, party.toString());
        transaction.expire(MEMBER_PREFIX + player, EXPIRATION_TIME);
        if(execute) transaction.exec();
    }

//    /**//TODO split user get out to own method and restore
//     * Removes a player from their own party
//     * @param source The player leaving their party
//     * @return Whether the operation was successful
//     */
//    public Future<PartyResult> leaveParty(UUID source){
//        return removePlayerFromParty(source, source);
//    }

    /**
     * Removes a player from their own party
     * @param source The disbanding their party
     * @return Whether the operation was successful
     */
    public Future<PartyResult> disbandParty(final UUID source) {
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        //Initial check
        if (source == null) {
            result.complete(new PartyResult(false, "qq.error.disband.party"));
            return result;
        }

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            JedisLock lock = null;
            Transaction t = null;
            try (Jedis jedis = RedisManager.getPool().getResource()) {
                //Get the users party and lock it upon getting it
                Party p = getUserParty(source).get(3, TimeUnit.SECONDS);
                lock = new JedisLock(jedis, LOCK_PREFIX + PARTY_PREFIX + p.getPartyID(), 3000, 10000);
                lock.acquire();
                if (p != null) {
                    //Check they own this party
                    if (source.equals(p.getLeader())) {
                        leaveAtlas(source).get(3, TimeUnit.SECONDS);

                        //Remove member mappings
                        Collection<UUID> all = p.getMembersWithLeader();
                        for (UUID member : all) jedis.watch(MEMBER_PREFIX + member);
                        t = jedis.multi();
                        for (UUID member : all) t.del(MEMBER_PREFIX + member);
                        t.del(PARTY_PREFIX + p.getPartyID());
                        t.exec();
                        t.close();

                        updateCaches(p, source);
                        sendCacheMessage(p.getPartyID(), source);
                        result.complete(new PartyResult(true, "qq.success.disband"));
                        //Update cache
                        for (UUID member : all) partyCacheByMember.remove(member);

                    } else {
                        result.complete(new PartyResult(false, "qq.error.not.leader.disband"));
                    }
                } else {
                    result.complete(new PartyResult(false, "qq.error.not.leader.disband"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.complete(new PartyResult(false, "qq.error.disband"));
            } finally {
                RedisManager.releaseLock(lock);
                if(t != null) t.clear();
            }
        });

        return result;
    }

    /**
     * Removes a target player from their party if the source player is that party's leader
     * @param source The source of the removal
     * @param targetUsername The username of the player to remove
     * @return Whether the operation was successful
     */
    public Future<PartyResult> removePlayerFromParty(final UUID source, final String targetUsername) {//TODO overload for uuid uuid
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        //Player name too long
        if(targetUsername.length() > 16){
            result.complete(new PartyResult(false, "qq.error.join.long.name.remove"));
            return result;
        }
        //Player name too short
        if(targetUsername.length() <= 0){
            result.complete(new PartyResult(false, "qq.error.join.short.name.remove"));
            return result;
        }
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            //Convert target username to UUID
            final UUID target;
            try {
                User u = new User().getByUsername(targetUsername);
                target = u.getId();
            } catch (APIException e) {
                result.complete(new PartyResult(false, "qq.error.remove"));
                e.printStackTrace();
                return;
            }

            Party sourceParty = partyCacheByMember.get(source);
            try {
                Party targetParty = partyCacheByMember.getOrDefault(target, getUserPartySync(target));//Will load target party if target is not local
                final boolean self = source.equals(target);
                //Prevent non-leader removing others
                if(!self){
                    if(!sourceParty.getLeader().equals(source)) {
                        result.complete(new PartyResult(false, "qq.error.not.leader.remove"));
                        return;
                    }
                    if(targetParty == null || !targetParty.getPartyID().equals(sourceParty.getPartyID())){
                        result.complete(new PartyResult(false, "qq.error.different.party.remove"));
                        return;
                    }
                }

                //Prevent leader being removed
                if(targetParty.getLeader().equals(target)) {
                    result.complete(new PartyResult(false, "qq.error.is.leader.remove"));
                    return;
                }
                JedisLock partyLock = null;
                JedisLock memberLock = null;
                Transaction t = null;
                try(Jedis jedis = RedisManager.getPool().getResource()){
                    String partyKey = PARTY_PREFIX + partyCacheByMember.get(target).getPartyID();
                    partyLock = new JedisLock(jedis, LOCK_PREFIX + partyKey, 3000, 10000);
                    partyLock.acquire();

                    //Check party still exists
                    if(jedis.exists(partyKey)) {
                        //Lock player object
                        String memberKey = MEMBER_PREFIX + target;
                        memberLock = new JedisLock(jedis, LOCK_PREFIX + memberKey, 3000, 10000);
                        memberLock.acquire();

                        //Remove the player
                        Party party = g.fromJson(jedis.get(partyKey), Party.class);
                        t = jedis.multi();
                        t.del(memberKey);
                        party.removeMember(target);
                        t.set(partyKey, g.toJson(party));
                        t.exec();

                        updateCaches(party, source);
                        sendCacheMessage(party.getPartyID(), source);
                        result.complete(new PartyResult(true, "qq.success.remove"));
                    }else{
                        result.complete(new PartyResult(false, "qq.error.no.party.remove"));
                    }
                }catch (Exception e){
                    result.complete(new PartyResult(false, "qq.error.remove"));
                }finally {
                    RedisManager.releaseLock(memberLock);
                    RedisManager.releaseLock(partyLock);
                    if(t != null) t.clear();
                }
            } catch (Exception e) {
                result.complete(new PartyResult(false, "qq.error.remove"));
            }
        });
        return result;
    }

    /**
     * Attempts to add a party to a queue. Will create a party for the sending user if they are not in one
     * @param gameId The game attempting to be queued for
     * @param queueingUser The user initiating the queueing
     * @return Whether the operation was successful
     */
    public Future<PartyResult> joinAtlas(final UUID gameId, final UUID queueingUser){
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        Future<Party> partyFuture = PartyManager.getInstance().getUserParty(queueingUser);
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                GameMode gameMode = new GameMode().get(gameId);
                if(gameMode.exists() && gameMode.isActive){//If its a real and active game mode
                    User u = new User().get(queueingUser);
                    Rank r = new Rank().get(u.rank_id);
                    boolean joinAsPriority = u.exists() && r.exists() && r.isPriority();
                    Party party = partyFuture.get(3, TimeUnit.SECONDS);
                    if(party == null){
                        createPartySync(queueingUser, null);
                        party = getCachedUserParty(queueingUser);
                        if(party == null){
                            result.complete(new PartyResult(false, "qq.error.enqueue"));
                        }
                    }

                    //Check they own the party
                    if(queueingUser.equals(party.getLeader())) {
                        // TODO update redis party
                        updateCaches(party, queueingUser);
                        new AtlasQueueParty(new GameJoinData(party, gameMode.id, joinAsPriority)).send();
                        result.complete(new PartyResult(true, "qq.success.enqueue"));
                    }else{
                        //TODO change to letting the player know their request has been sent to the party leader
                        result.complete(new PartyResult(false, "qq.error.not.leader.enqueue"));
                    }

                }else{
                    //TODO error - Try again later
                    result.complete(new PartyResult(false, "qq.error.enqueue.unavailable"));
                }

            } catch (Exception e) {
                result.complete(new PartyResult(false, "qq.error.enqueue"));
                e.printStackTrace();
            }
        });
//        if(QuickQueue.partyMembers.get(senderUUID) == null) CreateParty();
//        if(!QueueAPI.getMember(senderUUID).getParty().isQueued()){}
        return result;
    }

    /**
     * Attempts to remove a party from any queue.
     * @param dequeueingUser The user initiating the queue leave
     * @return Whether the operation was successful
     */
    public Future<PartyResult> leaveAtlas(final UUID dequeueingUser){
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        Future<Party> partyFuture = PartyManager.getInstance().getUserParty(dequeueingUser);
        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                Party party = partyFuture.get(3, TimeUnit.SECONDS);
                //Check player is in party
                if(party == null){
                    result.complete(new PartyResult(false, "qq.error.no.party.dequeue"));
                    return;
                }

                //Check they own the party
                if(dequeueingUser.equals(party.getLeader())) {//TODO check they're queued
                    // TODO update redis party
                    updateCaches(party, dequeueingUser);
                    new AtlasDequeueParty(new GameLeaveData(party)).send();
                    result.complete(new PartyResult(true, "qq.success.dequeue"));
                }else{
                    result.complete(new PartyResult(false, "qq.error.not.leader.dequeue"));
                }
            } catch (Exception e) {
                result.complete(new PartyResult(false, "qq.error.enqueue"));
                e.printStackTrace();
            }
        });
        return result;
    }

    /**
     * Accept an invite to a party
     * @param setter The party member queueing the party
     * @param gamemode The gamemode they're in, null if they left the game
     * @return Whether the operation was successful
     */
    public Future<PartyResult> setPartyQueue(final UUID setter, final GameMode gamemode){
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        Future<Party> partyFuture = PartyManager.getInstance().getUserParty(setter);

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            JedisLock partyLock = null;
            try (Jedis jedis = RedisManager.getPool().getResource()) {
                Party party = partyFuture.get(3, TimeUnit.SECONDS);
                String partyKey = PARTY_PREFIX + party.getPartyID();

                //Lock party object
                partyLock = new JedisLock(jedis, LOCK_PREFIX + partyKey, 3000, 10000);
                partyLock.acquire();

                //Check party still exists
                if(jedis.exists(partyKey)){
                    //Update party queue state
                    Party targetParty = g.fromJson(jedis.get(partyKey), Party.class);
                    targetParty.setQueued(gamemode != null);
//                    targetParty.setQueuedGame(gamemode);

                    updateCaches(targetParty, setter);
                    sendCacheMessage(targetParty.getPartyID(), setter);
                    result.complete(new PartyResult(true, "qq.queue.setgame"));
                } else {
                    result.complete(new PartyResult(false, "qq.error.queue.setgame"));
                }
            } catch (Exception e) {
                result.completeExceptionally(e);
            } finally {
                RedisManager.releaseLock(partyLock);
            }
        });

        return result;
    }

    /**
     * Sends a party invite to a player
     * @param source The player sending the invite
     * @param target The player receiving the invite
     * @return Whether the operation was successful
     */
    public Future<PartyResult> invitePlayer(final Player source, final UUID target){//TODO allow users to block invites
        CompletableFuture<PartyResult> result = new CompletableFuture<>();//TODO prevent self invite
        //Check player isn't somehow null
        if(source == null){//TODO usage?
            result.complete(new PartyResult(false, "qq.error"));
            return result;
        }
        //Check player is partied
        Party party = getCachedUserParty(source.getUniqueId());
        if(party == null){
            result.complete(new PartyResult(false, "qq.error.no.party.invite"));
            return result;
        }
        //Check player is not inviting themselves
        if(source.getUniqueId().equals(target)) {
            result.complete(new PartyResult(false, "qq.error.invite.self"));
            return result;
        }
        //Check player is party leader
        if(!source.getUniqueId().equals(party.getLeader())) {
            result.complete(new PartyResult(false, "qq.error.not.leader.invite"));
            return result;
        }

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {//TODO online check //TODO check target not in party
            try (Jedis jedis = RedisManager.getPool().getResource()) {
                //Check target isn't in a party
                if(jedis.exists(MEMBER_PREFIX + target)){
                    result.complete(new PartyResult(false, "qq.error.invite.target.partied"));
                    return;
                }

                String partySideKey = INVITE_PREFIX + party.getPartyID() + "." + target;
                String userSideKey = INVITE_PREFIX + target + "." + source.getName();

                //Check they aren't pending an invite (prevent spam)
                if(jedis.exists(partySideKey)){//Inform user they must wait
                    result.complete(new PartyResult(false, "qq.error.invite.pending"));
                    return;
                }
                //Create the invite
                jedis.set(partySideKey, g.toJson(new Invite(party.getPartyID(), source.getUniqueId())));
                System.out.println("setting " + partySideKey);
                jedis.set(userSideKey, g.toJson(new Invite(party.getPartyID(), source.getUniqueId())));
                System.out.println("setting " + userSideKey);
                jedis.expire(partySideKey, 60);
                jedis.expire(userSideKey, 60);

                //TODO swap to message with clickable join button
                MessageAPI.sendMessage(target, Lang.replaceUnparsedTag(Lang.get("qq.info.invite.received", target), "source.name", source.getName()), false);
                result.complete(new PartyResult(true, "qq.success.invite"));

            } catch (Exception e) {
                result.complete(new PartyResult(false, "qq.error.invite"));
                e.printStackTrace();
            }
        });

        return result;
    }

    /**
     * Accept an invite to a party
     * @param partyLeader The party invite sender
     * @param newPlayer The player attempting to join the party
     * @return Whether the operation was successful
     */
    public Future<PartyResult> joinParty(final UUID newPlayer, final String partyLeader){
        CompletableFuture<PartyResult> result = new CompletableFuture<>();
        //Check player isn't somehow null
        if(newPlayer == null){
            result.complete(new PartyResult(false, "qq.error"));
            return result;
        }
        //Player name too long
        if(partyLeader.length() > 16){
            result.complete(new PartyResult(false, "qq.error.join.long.name.invite"));
            return result;
        }
        //Player name too short
        if(partyLeader.length() <= 0){
            result.complete(new PartyResult(false, "qq.error.join.short.name.invite"));
            return result;
        }

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {//TODO online check //TODO check new player not in party
            JedisLock partyLock = null;
            JedisLock memberLock = null;
            Transaction t = null;
            try (Jedis jedis = RedisManager.getPool().getResource()) {
                String inviteKey = INVITE_PREFIX + newPlayer + "." + partyLeader;
                //Check their invite exists and get the data for it if it does
                String inviteString = jedis.get(inviteKey);
                if(inviteString == null){
                    //Invite no longer exists
                    result.complete(new PartyResult(false, "qq.error.join.invite.expired"));
                    return;
                }
                if(jedis.exists(MEMBER_PREFIX + newPlayer)){
                    //Player already in party
                    result.complete(new PartyResult(false, "qq.error.join.in.party"));
                    return;
                }

                //Load the party from the invite data
                Invite invite = g.fromJson(inviteString, Invite.class);
                String partyKey = PARTY_PREFIX + invite.getSourcePartyID();
                //Lock party object
                partyLock = new JedisLock(jedis, LOCK_PREFIX + partyKey, 3000, 10000);
                partyLock.acquire();
                //Check party still exists
                if(jedis.exists(partyKey)){
                    String memberKey = MEMBER_PREFIX + newPlayer;
                    //Lock player object
                    memberLock = new JedisLock(jedis, LOCK_PREFIX + memberKey, 3000, 10000);
                    memberLock.acquire();
                    //Add player to party
                    Party targetParty = g.fromJson(jedis.get(partyKey), Party.class);
                    t = jedis.multi();
                    targetParty.addMember(newPlayer);
                    t.set(partyKey, g.toJson(targetParty));
                    setPlayerPartySync(t, newPlayer, targetParty.getPartyID(), true);
                    t.close();

                    jedis.del(inviteKey);
                    jedis.del(INVITE_PREFIX + targetParty.getPartyID() + "." + newPlayer);

                    updateCaches(targetParty, newPlayer);
                    sendCacheMessage(targetParty.getPartyID(), newPlayer);
                    result.complete(new PartyResult(true, "qq.success.join"));
                } else {
                    result.complete(new PartyResult(false, "qq.error.join.invite.expired"));
                }
            } catch (Exception e) {
                result.completeExceptionally(e);
            } finally {
                RedisManager.releaseLock(partyLock);
                RedisManager.releaseLock(memberLock);
                if(t != null) t.clear();
            }
        });

        return result;
    }

    ////
    //Cache and expire updating methods
    ////
    public static ConcurrentHashMap<UUID, BukkitTask> updatingParties = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, Party> partyCacheByMember = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, Party> partyCacheById = new ConcurrentHashMap<>();

    private static void sendCacheMessage(UUID party, UUID exclude){
        if(exclude != null){
            //TODO
        }

        //TODO
    }

    private static void updateCaches(Party p, UUID user){
        if(user != null) partyCacheByMember.put(user, p);
        if(!partyCacheById.containsKey(p.getPartyID())) Message.bindKey("qq.party."+p.getPartyID());
        partyCacheById.put(p.getPartyID(), p);
    }

    public static Party getCachedUserParty(UUID player){
        return partyCacheByMember.get(player);
    }

    public static Party getCachedParty(UUID partyId){
        return partyCacheById.get(partyId);
    }

    public void haltExpiration(final UUID party){
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(QuickQueue.getInstance(), () -> {
            Transaction t = null;
            try(Jedis jedis = RedisManager.getPool().getResource()){
                //Get the party
                String rawParty = jedis.get(PARTY_PREFIX + party);
                if(rawParty != null){
                    Party p = g.fromJson(rawParty, Party.class);
                    if(Bukkit.getPlayer(p.getLeader()) != null) {
                        //Update expiration on keys
                        t = jedis.multi();
                        t.expire(PARTY_PREFIX + party, EXPIRATION_TIME);//TODO locking checks?
                        t.expire(MEMBER_PREFIX + p.getLeader(), EXPIRATION_TIME);
                        for(UUID member : p.getMembers()) t.expire(MEMBER_PREFIX + member, EXPIRATION_TIME);
                        t.exec();

                        if (p != null) {//TODO fix
                            //Update partyCacheByMember cache
//                            if (playerId.equals(p.getLeader())) {
//                                partyCacheByMember.put(playerId, p);
//                            } else if (partyCacheByMember.contains(playerId)) {
//                                partyCacheByMember.remove(playerId);
//                            }
                        }
                    }else{
                        resumeExpiration(party);
                        //TODO more
                    }
                }else{
                    //Party likely disbanded
                    resumeExpiration(party);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
//                RedisManager.releaseLock(lock);
                if(t != null) t.clear();
            }
        }, 0, 20 * EXPIRATION_TIME/5);

        updatingParties.put(party, task);
    }

    public void resumeExpiration(UUID partyId){
        BukkitTask t = updatingParties.get(partyId);
        if(t != null) t.cancel();
        //TODO remove partyCacheByMember if need be
    }

    public void clearPlayerCache(final UUID player){
        boolean isLast = true;
        Party p = partyCacheByMember.get(player);
        if(p!=null) {
            for (UUID member : p.getMembersWithLeader()) {
                if (!member.equals(player)) isLast = false;
            }
            if (isLast) {
                partyCacheById.remove(p.getPartyID());
                Message.unbindKey("qq.party." + p.getPartyID());
                //TODO destroy rabbit keys on last party member leave
            }
        }
        partyCacheByMember.remove(player);
    }
}
