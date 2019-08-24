package network.marble.dataaccesslayer.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

class GetServerName implements PluginMessageListener, Listener{
    Plugin p;

    public GetServerName(Plugin p){
        this.p = p;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
         if (!channel.equals("BungeeCord")) {
              return;
         }

         ByteArrayDataInput in = ByteStreams.newDataInput(message);
         DataAccessLayer.setServerName(in.readUTF());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerJoinEvent event) {//TODO get party and invite information from hermes is available
        if(DataAccessLayer.getServerName() == null){
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GetServer");
            event.getPlayer().sendPluginMessage(p, "BungeeCord", out.toByteArray());
        }
    }
}