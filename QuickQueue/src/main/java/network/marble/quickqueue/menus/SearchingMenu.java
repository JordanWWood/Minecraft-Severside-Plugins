package network.marble.quickqueue.menus;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.packetwrapper.WrapperPlayClientUpdateSign;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.actions.InvitePlayer;
//TODO DESTROY THE LISTENERS FOR SIGNS
public class SearchingMenu extends Menu{
    int minimumCharacters;
    Player p;

    Material priorMaterial;//For block restoration
    private Map<String, SignGUIListener> listeners;
    private ProtocolManager protocolManager;
    private Map<String, Vector> signLocations;

    public SearchingMenu(Player p, InventoryItem inventoryItem, int inventorySize, int minimumCharacters, String menuTitle) {
        super(p, inventoryItem, inventorySize);
        this.p = p;


        protocolManager = ProtocolLibrary.getProtocolManager();
        listeners = new ConcurrentHashMap<>();
        signLocations = new ConcurrentHashMap<>();

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(QuickQueue.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        final Player player = event.getPlayer();
                        Vector v = signLocations.remove(player.getName());
                        BlockPosition bp = event.getPacket().getBlockPositionModifier().getValues().get(0);

                        WrapperPlayClientUpdateSign wrappedPacket = new WrapperPlayClientUpdateSign(event.getPacket());
                        
                        final String[] lines = new String[4];
                        final WrappedChatComponent[] wrappedLines = wrappedPacket.getLines();
                        final SignGUIListener response = listeners.remove(event.getPlayer().getName());

                        for(int i = 0; i < wrappedLines.length; i++) {
                            lines[i] = wrappedLines[i].getJson();
                        }

                        if (v != null) {
                            if (bp.getX() != v.getBlockX()) return;
                            if (bp.getY() != v.getBlockY()) return;
                            if (bp.getZ() != v.getBlockZ()) return;
                            p.sendBlockChange(new Location(p.getWorld(), v.getX(), v.getY(), v.getZ()), priorMaterial, (byte)0);
                        }
                        if (response != null) {
                            event.setCancelled(true);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> response.onSignDone(player, lines));//Move back to main thread
                        }
                    }
                }
        );
        
        open(p, new String[] { "", "^^^^^", "Type search", "above" }, (player, lines) -> {
            p.closeInventory();
            
            if(lines[0].length() >= minimumCharacters){
                String name = lines[0].substring(1, lines[0].length()-1);
                Player targetPlayer = Bukkit.getPlayer(name);
                InvitePlayer.getInstance().executeAction(p, null, new String[]{targetPlayer != null ? targetPlayer.getName() : name});
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void open(Player player, String[] defaultText, SignGUIListener response) {
        int x = player.getLocation().getBlockX();
        int y = 0;
        int z = player.getLocation().getBlockZ();
        
        Location l = new Location(p.getWorld(), x, y, z);
        priorMaterial = l.getBlock().getType();
        p.sendBlockChange(l, Material.WALL_SIGN, (byte)0);//Fake sign placement
        p.sendSignChange(l, defaultText);//Fill sign with text
        
        BlockPosition bpos = new BlockPosition(x, y, z);
        PacketContainer packet133 = protocolManager.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        packet133.getBlockPositionModifier().write(0, bpos);

        try {
            protocolManager.sendServerPacket(player, packet133);
            signLocations.put(player.getName(), new Vector(x, y, z));
            listeners.put(player.getName(), response);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public interface SignGUIListener {
        public void onSignDone(Player player, String[] lines);
    }

    @Override
    public boolean execute(int slot, int rawSlot) {
        return false;
    }
}
