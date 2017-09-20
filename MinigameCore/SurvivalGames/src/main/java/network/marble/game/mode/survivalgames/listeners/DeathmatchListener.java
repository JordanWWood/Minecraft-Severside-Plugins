package network.marble.game.mode.survivalgames.listeners;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathmatchListener implements Listener {
    @Getter @Setter private static int currentDamageRadius = 30;
    private Map<UUID, Long> lastStruck = new HashMap<>();
    Vector dmMid = StateListener.mapConfig.getGame().getDeathMatch().getCenter();
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();

        if (p.getGameMode() == GameMode.SPECTATOR) return;

        Vector pLocation = p.getLocation().toVector();

        if (!liesInCircle(pLocation.getX(), pLocation.getZ(), dmMid.getX(), dmMid.getZ(), currentDamageRadius)) {
            if (!lastStruck.containsKey(p.getUniqueId()) || (lastStruck.get(p.getUniqueId()) + 1000L < System.currentTimeMillis())) {
                event.getPlayer().getWorld().strikeLightning(p.getLocation());

                lastStruck.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    // Stolen from somewhere by Richie. Pitchforks should be directed at him
    // It wouldn't have been stealing if they hadn't found out, look what you've done
    /**
     * @param x  The x coordinate of the point to test.
     * @param y  The y coordinate of the point to test.
     * @param cX The x coordinate of the center of the circle.
     * @param cY The y coordinate of the center of the circle.
     * @param r  The radius of the circle.
     */
    public static boolean liesInCircle(double x, double y, double cX, double cY, double r)
    {
        double dx = x - cX;
        double dy = y - cY;
        return dx * dx + dy * dy <= r * r;
    }
}
