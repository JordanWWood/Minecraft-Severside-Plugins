package network.marble.quickqueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import network.marble.quickqueue.commands.PartyCommands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.models.Game;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.quickqueue.api.QueueAPI;
import network.marble.quickqueue.listeners.QueueListener;
import network.marble.quickqueue.menus.Menus;
import network.marble.quickqueue.messages.Message;
import network.marble.quickqueue.parties.InviteList;
import network.marble.quickqueue.parties.Party;

public class QuickQueue extends JavaPlugin {
    @Getter
    private static QuickQueue instance;
//  public static Map<Player, BossBar> bossBars = new HashMap<Player, BossBar>();
    
    public static HashMap<UUID, GameSet> games = new HashMap<>();
    public static Map<UUID, Party> parties = new ConcurrentHashMap<>();//List of parties by their unique party ID
    public static Map<UUID, InviteList> invites = new ConcurrentHashMap<>(); //List of invites for each player
    
    public static String serverName = null;//Loaded on the first player join
    
    @Override
    public void onEnable() {
            instance = this;
            loadGames();
            registerEvents();
            this.getCommand("party").setExecutor(new PartyCommands());
            Menus.buildInventoryMenus();
//            registerBossBarManager();
            buildInventoryTypes();
//            activateSchedule();//TODO handle player bossbars
//            registerBadges();
            Message.startQueueConsumer();
            getLogger().info("QuickQueue successfully loaded.");
    }
    
    private void loadGames() {
        try {
            new Game().get().forEach(g -> {
                if(g.isActive) games.computeIfAbsent(g.id, gs -> new GameSet(g));
            });
            new GameMode().get().forEach(g -> {
                if (games.containsKey(g.game_id) && g.isLive) games.get(g.game_id).getModes().add(g);
            });
            ArrayList<UUID> remove = new ArrayList<>();
            games.forEach((u,g) -> {if(g.modes.isEmpty()) remove.add(u);});
            for(UUID u: remove) games.remove(u);
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        getLogger().info("QuickQueue Disabled.");
    }
    
    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new QueueListener(), this);
    }
    
//    private void registerBossBarManager() {
        //ArrayList<IdentifiablePluginString> strings = new ArrayList<IdentifiablePluginString>();
        //BossBarManagerAPI.registerPlugin(getName(), 100);

//        if(games!=null){
//            try(Jedis jedis = QuickQueue.pool.getResource()){
//                for(Minigames mg : minigames){
//                    String gameID = mg.getId().toString();
//                    String key = "gameavgwait:" + gameID;
//
//                    String string = ChatColor.GOLD + "Approximate wait for ";
//                    string += ChatColor.GREEN + mg.getMinigameName();
//                    string += ChatColor.GOLD + ": ";
//                    if(jedis.exists(key)){
//                        String avgTimeString = jedis.get(key);
//                        String finalAvgTimeString;
//
//                        double waitSeconds = Integer.valueOf(avgTimeString);
//                        if(waitSeconds>0){
//                            double waitMinutes = Math.ceil(waitSeconds/60);
//                            finalAvgTimeString = "\u2248" + (int)waitMinutes + " minute";
//                            if((int)waitMinutes > 1){
//                                finalAvgTimeString += "s";
//                            }
//                            finalAvgTimeString += ".";
//                        }else{
//                            finalAvgTimeString = "nearly instant.";
//                        }
//                        string += finalAvgTimeString;
//
//                    }else{
//                        string += "calculating...";
//                    }
//                    strings.add(new IdentifiablePluginString(gameID, new PluginString(getName(), string)));
//                }
//
//                BossBarManagerAPI.updateStrings(getName(), strings);
//            }catch(Exception e){
//                getLogger().severe("Failed to load at BBM registration.");
//                e.printStackTrace();
//                this.setEnabled(false);
//            }
//        }
//    }

    private void buildInventoryTypes() {
        for (Player player : getServer().getOnlinePlayers()) {
            QueueAPI.applyAppropriateInventoryToPlayer(player);
        }
    }

//    private void activateSchedule() {
//        getServer().getScheduler().scheduleSyncRepeatingTask(this, new BarStringCollector(), 0L, 20*1000);//TODO remove when rabbit version is done
//    }

//    private void registerBadges() {
//        try {
//            BadgeAPI.createBadgeIfNotExists(getName(), "Party Animal", "Join or create 100 parties.", 100, false, false);
//        } catch (APIException e) {
//            e.printStackTrace();
//        }
//    }
}
