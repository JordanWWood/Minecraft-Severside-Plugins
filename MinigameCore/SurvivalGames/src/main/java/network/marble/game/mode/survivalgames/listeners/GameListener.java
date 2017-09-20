package network.marble.game.mode.survivalgames.listeners;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import network.marble.currencyapi.api.CurrencyAPI;
import network.marble.currencyapi.api.KnownCurrency;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import network.marble.game.mode.survivalgames.config.Model.Locations;
import network.marble.game.mode.survivalgames.events.DeathmatchStartEvent;
import network.marble.game.mode.survivalgames.managers.LootManager;
import network.marble.game.mode.survivalgames.utils.FontFormat;
import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.events.player.PlayerDisconnectEvent;
import network.marble.minigamecore.entities.game.GameStatus;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.AnalyticsManager;
import network.marble.minigamecore.managers.EventManager;
import network.marble.minigamecore.managers.GameManager;
import network.marble.minigamecore.managers.PlayerManager;
import network.marble.minigamecore.managers.TimerManager;
import network.marble.scoreboards.entities.scoreboard.Scoreboard;
import network.marble.scoreboards.entities.scoreboard.row.TextRow;
import network.marble.scoreboards.managers.ScoreboardManager;

public class GameListener implements Listener {
    private static boolean isDeathmatch = false;
    private static boolean startedDeathmatch = false;
    private static UUID timer, lightningRingTimer;

    //Player tracking
    public static ArrayList<UUID> deadPlayers = new ArrayList<>();
    public static ArrayList<UUID> alivePlayers = new ArrayList<>();
    public static Player winner;

    public static UUID playingCount, watchingCount;//Player rows
    public static Scoreboard gameBoard, deathmatchCountIn, deathmatch, winnerBoard;//Scoreboards
    public static UUID deathmatchClock, playersLeftAlive, playersNotAlive;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();
            int damageInt = (int) e.getFinalDamage();

            UUID playerID = PlayerManager.getPlayer(p).getUserId();
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(playerID, StateListener.mapConfig.getInfo().getName() + ".takendamage", damageInt);
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(playerID, "takendamage", damageInt);

            UUID damagerID = PlayerManager.getPlayer(d).getUserId();
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(damagerID, StateListener.mapConfig.getInfo().getName() + ".dealtdamage", damageInt);
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(damagerID, "dealtdamage", damageInt);
            if (p.getHealth() - e.getFinalDamage() <= 0 && p.getGameMode() == GameMode.SURVIVAL) {
                awardChips(d, 2, "Killed " + p.getName(), "Killed " + p.getName());

                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(damagerID, StateListener.mapConfig.getInfo().getName() + ".kills", 1);
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(damagerID, "kills", 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            if (p.getHealth() - e.getFinalDamage() <= 0 && p.getGameMode() == GameMode.SURVIVAL) {
                e.setCancelled(true);
                killPlayer(p, PlayerManager.getPlayer(p).playerType, true);
                deadPlayerPacketEffect(p);
                UUID userID = PlayerManager.getPlayer(p).getUserId();
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".losses", 1);
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "losses", 1);
                long life = System.currentTimeMillis() - StateListener.getStartTime();
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".lifespan", life);
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "lifespan", life);
            }
        }
    }

    private void deadPlayerPacketEffect(Player deadPlayer) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) deadPlayer.getWorld()).getHandle();

        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, ((CraftPlayer) deadPlayer).getHandle().getProfile(), new PlayerInteractManager(nmsWorld));

        npc.setLocation(deadPlayer.getLocation().getX(), deadPlayer.getLocation().getY(), deadPlayer.getLocation().getZ(), deadPlayer.getLocation().getYaw(), deadPlayer.getLocation().getPitch());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(deadPlayer.getUniqueId())) {
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                connection.sendPacket(new PacketPlayOutEntityStatus(npc, (byte) 3));
            }
        }

        Bukkit.getScheduler().runTaskLater(MiniGameCore.instance, () -> {
            EntityPlayer ep = new EntityPlayer(nmsServer, nmsWorld, ((CraftPlayer) deadPlayer).getHandle().getProfile(), new PlayerInteractManager(nmsWorld));
            ep.playerInteractManager.setGameMode(EnumGamemode.SPECTATOR);
        }, 3 * 20);
    }

    static void killPlayer(Player p, PlayerType type, boolean isDeath) {
        if (type == PlayerType.PLAYER) {
            alivePlayers.remove(p.getUniqueId());
            if (!deadPlayers.contains(p.getUniqueId())) deadPlayers.add(p.getUniqueId());
            else if (!isDeath) deadPlayers.remove(p.getUniqueId());
            if (isDeath) {
                p.getWorld().strikeLightningEffect(p.getLocation());
                for (ItemStack item : p.getInventory()) {
                    if (item == null) continue;
                    p.getWorld().dropItem(p.getLocation(), item);
                }
                p.getInventory().clear();
                p.setGameMode(GameMode.SPECTATOR);

                Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix() + "&2A cannon can be heard in the distance..."));
                Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix() + "[&6" + alivePlayers.size() + "&8] &2players remain!"));
            }
        }
        if (!startedDeathmatch) {
            if (gameBoard != null) {
                ((TextRow) gameBoard.getRow(playingCount)).setText("Alive: " + GameListener.alivePlayers.size());
                ((TextRow) gameBoard.getRow(watchingCount)).setText("Watching: " + (GameListener.deadPlayers.size() + PlayerManager.getPlayers(PlayerType.SPECTATOR).size()));
                ScoreboardManager.getInstance().updateScoreboard();
            }
        } else {
            ((TextRow) deathmatch.getRow(playersLeftAlive)).setText("Alive: " + GameListener.alivePlayers.size());
            ((TextRow) deathmatch.getRow(playersNotAlive)).setText("Watching: " + (GameListener.deadPlayers.size() + PlayerManager.getPlayers(PlayerType.SPECTATOR).size()));
        }

        if (alivePlayers.size() == 1) {
            winner = Bukkit.getPlayer(alivePlayers.get(0));
            UUID userID = PlayerManager.getPlayer(p).getUserId();
            awardChips(p, 20, "Won a game of SG", "You won");

            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".wins", 1);
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "wins", 1);
            long life = System.currentTimeMillis() - StateListener.getStartTime();
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".lifespan", life);
            AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "lifespan", life);

            GameManager.setStatus(GameStatus.FINISHED);
            if (timer != null) TimerManager.getInstance().stopTimer(timer);
        } else if (alivePlayers.size() <= 3 && !isDeathmatch) {
            isDeathmatch = true;
            ((TextRow) deathmatchCountIn.getRow(deathmatchClock)).setText("01:00");
            ScoreboardManager.getInstance().setScoreboard(deathmatchCountIn);
            timer = TimerManager.getInstance().runEveryUntil((t, b) -> {
                long timeRemianing = t.executeUntil - System.currentTimeMillis();
                ((TextRow) deathmatchCountIn.getRow(deathmatchClock)).setText(String.format("%1$tM:%1$tS", timeRemianing));
                if (!b) {
                    int timeSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeRemianing);
                    if (timeSeconds == 60 || timeSeconds == 30 || timeSeconds == 15 || timeSeconds == 10 || timeSeconds <= 5)
                        Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix() + "[&6" + (TimeUnit.MILLISECONDS.toSeconds(timeRemianing)) + "&8] &2seconds until deathmatch!"));
                } else {
                    Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
                        ((TextRow) deathmatchCountIn.getRow(deathmatchClock)).setText("00:10");
                        ScoreboardManager.getInstance().setScoreboard(deathmatchCountIn);
                        ScoreboardManager.getInstance().updateScoreboard();
                    });
                    Bukkit.getServer().getPluginManager().callEvent(new DeathmatchStartEvent());
                }
            }, 1L, TimeUnit.SECONDS, 62L, TimeUnit.SECONDS);//TODO Temporarily like this due to timer skip bug
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        if (!deadPlayers.contains(e.getMiniGamePlayer().id)) {
            killPlayer(e.getMiniGamePlayer().getPlayer(), e.getMiniGamePlayer().playerType, false);
            if (e.getMiniGamePlayer().getPlayerType() == PlayerType.PLAYER) {
                UUID userID = e.getMiniGamePlayer().getUserId();
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".losses", 1);
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "losses", 1);
                long life = System.currentTimeMillis() - StateListener.getStartTime();
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".lifespan", life);
                AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "lifespan", life);
            }
        }
        deadPlayers.remove(e.getMiniGamePlayer().id);
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.FIRE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onUnArt(HangingBreakByEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() == Material.DISPENSER) {
            e.setCancelled(true);
            e.getPlayer().closeInventory();
            return;
        }
        if (e.getClickedBlock().getType() == Material.CHEST) {
            if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                e.setCancelled(true);
                return;
            }
            Location location = e.getClickedBlock().getLocation();
            Chest chest = (Chest) e.getClickedBlock().getState();

            for (Locations loc : StateListener.getMapConfig().getChests().getLocations()) {
                Location location1 = new Location(e.getPlayer().getWorld(), loc.getX(), loc.getY(), loc.getZ());

                if (location.equals(location1)) {
                    LootManager.rollLoot(loc, 6, 10, chest.getInventory().getSize() == 54, e.getPlayer());
                    return;
                }
            }

            chest.getInventory().clear();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(StateListener.mapConfig.getPodiumLocation(1));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.LEAVES) return;
        if (e.getBlock().getType() == Material.LEAVES_2) return;
        if (e.getBlock().getType() == Material.LONG_GRASS) return;
        if (e.getBlock().getType() == Material.CHORUS_FLOWER) return;
        if (e.getBlock().getType() == Material.YELLOW_FLOWER) return;
        if (e.getBlock().getType() == Material.RED_ROSE) return;
        if (e.getBlock().getType() == Material.VINE) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player source = e.getPlayer();
        sendMessage(StateListener.mapConfig.getChat().getChatDistance(), e.getMessage(), source.getDisplayName(), source.getLocation());
        e.setCancelled(true);
    }

    public static void sendMessage(int radius, String message, String sender, Location source) {
        int maxDistanceSquared = radius * radius;
        for (MiniGamePlayer mp : PlayerManager.getPlayers(PlayerType.PLAYER)) {
            Player p = mp.getPlayer();
            if ((p.getLocation().distanceSquared(source) < maxDistanceSquared)) {
                p.sendMessage("<" + sender + ChatColor.RESET + "> " + message);
            }
        }
        for (MiniGamePlayer mp : PlayerManager.getPlayers(PlayerType.ADMINISTRATOR)) {
            mp.getPlayer().sendMessage("<" + sender + ChatColor.RESET + ">" + message);
        }
        for (MiniGamePlayer mp : PlayerManager.getPlayers(PlayerType.MODERATOR)) {
            mp.getPlayer().sendMessage("<" + sender + ChatColor.RESET + ">" + message);
        }
    }

    @EventHandler
    public void onDeathmatchStart(DeathmatchStartEvent e) {
        Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
            int i = 0;
            for (UUID alive : alivePlayers) {
                if (alive == null) {
                    alivePlayers.remove(alive);
                    continue;
                }
                Player p = Bukkit.getPlayer(alive);
                if (p != null) {
                    p.teleport(new Location(p.getWorld(),
                            StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getX(),
                            StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getY(),
                            StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getZ(),
                            StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getYaw(),
                            StateListener.getMapConfig().getSpawns().getDeathmatch().get(i).getPitch()
                    ));
                    i+= StateListener.getMapConfig().getSpawns().getDeathmatch().size() / alivePlayers.size();
                    if(i == StateListener.getMapConfig().getSpawns().getDeathmatch().size()) i = 0;
                    UUID userID = PlayerManager.getPlayer(p).getUserId();
                    awardChips(p, 5, "Made it to deathmatch", "You made it to deathmatch");

                    AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, StateListener.mapConfig.getInfo().getName() + ".deathmatches", 1);
                    AnalyticsManager.getInstance().alterGameModeAnalyticsValue(userID, "deathmatches", 1);
                } else {//safety catch
                    alivePlayers.remove(alive);
                }
            }
        });
        LootManager.refillChests();
        DeathmatchListener.setCurrentDamageRadius(StateListener.getMapConfig().getGame().getDeathMatch().getRadius());
        
        Location l = new Location(Bukkit.getWorld(StateListener.currentGameWorld),
                StateListener.getMapConfig().getSpawns().getDeathmatch().get(0).getX(),
                StateListener.getMapConfig().getSpawns().getDeathmatch().get(0).getY(),
                StateListener.getMapConfig().getSpawns().getDeathmatch().get(0).getZ(),
                StateListener.getMapConfig().getSpawns().getDeathmatch().get(0).getYaw(),
                StateListener.getMapConfig().getSpawns().getDeathmatch().get(0).getPitch()
        );
        for (UUID dead : deadPlayers) {
            Player p = Bukkit.getPlayer(dead);
            if(p!=null)
            	p.teleport(l);
        }
        for (MiniGamePlayer p : PlayerManager.getPlayers(PlayerType.SPECTATOR)) {
            p.getPlayer().teleport(l);
        }

        EventManager.getInstance().unregisterEvent(StateListener.getGameListener());
        EventManager.getInstance().registerEvent(StateListener.getPreStartListener());

        TimerManager.getInstance().runEveryUntil((t, b) -> {
            long timeRemianing = t.executeUntil - System.currentTimeMillis();
            ((TextRow) deathmatchCountIn.getRow(deathmatchClock)).setText(String.format("%1$tM:%1$tS", timeRemianing));
            ScoreboardManager.getInstance().updateScoreboard();
            if (!b) {
                Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix() + "[&6" + (TimeUnit.MILLISECONDS.toSeconds(timeRemianing)) + "&8] &2seconds until deathmatch begins!"));
            } else {
                Bukkit.getScheduler().runTask(MiniGameCore.instance, () -> {
                    playersLeftAlive = deathmatch.addRow(new TextRow("Alive: " + alivePlayers.size()));
                    playersNotAlive = deathmatch.addRow(new TextRow("Watching:" + alivePlayers.size()));
                    ScoreboardManager.getInstance().setScoreboard(deathmatch);
                    startedDeathmatch = true;
                });
                Bukkit.broadcastMessage(FontFormat.translateString(StateListener.getPrefix() + "&2Deathmatch has begun!"));
                EventManager.getInstance().unregisterEvent(StateListener.getPreStartListener());
                EventManager.getInstance().registerEvent(StateListener.getGameListener());
            }
        }, 1L, TimeUnit.SECONDS, 12L, TimeUnit.SECONDS);//TODO Temporarily like this due to timer skip bug


        lightningRingTimer = TimerManager.getInstance().runEvery((t, b) -> {
            if (DeathmatchListener.getCurrentDamageRadius() <= 0)
                TimerManager.getInstance().stopTimer(lightningRingTimer);
            DeathmatchListener.setCurrentDamageRadius(DeathmatchListener.getCurrentDamageRadius() - 1);
        }, 10L, TimeUnit.SECONDS);

        StateListener.setDeathmatchListener(new DeathmatchListener());
        EventManager.getInstance().registerEvent(StateListener.getDeathmatchListener());
    }

    private static void awardChips(Player player, long amount, String reason, String displayText) {
    	try{
        CurrencyAPI.giveCurrency(player.getUniqueId(), CurrencyAPI.getCurrency(KnownCurrency.CHIPS.getName()), 2, reason);

        player.sendMessage(ChatColor.AQUA+displayText + ": "+ChatColor.GOLD + "+" +amount + ChatColor.AQUA+" chips!");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}