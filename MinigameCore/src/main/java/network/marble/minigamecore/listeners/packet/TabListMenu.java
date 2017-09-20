package network.marble.minigamecore.listeners.packet;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;

import network.marble.minigamecore.MiniGameCore;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import network.marble.minigamecore.entities.player.PlayerType;
import network.marble.minigamecore.managers.PlayerManager;

public class TabListMenu extends PacketAdapter {
    public TabListMenu() {
        super(MiniGameCore.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
            MiniGamePlayer player = PlayerManager.getPlayer(event.getPlayer());
            List<PlayerInfoData> players = new ArrayList<>();
            if (packet.getAction() != EnumWrappers.PlayerInfoAction.REMOVE_PLAYER) {
                packet.getData().forEach(info -> {
                    MiniGamePlayer packetPlayer = PlayerManager.getPlayer(info.getProfile().getUUID());
                    if (packetPlayer == null)
                        MiniGameCore.logger.warning("Player (TabListMenu:30) was null when checking TabListMenu, assumed player should be hidden from other player");
                    if (packetPlayer != null && canSeeOtherType(player.playerType, packetPlayer.playerType))
                        players.add(info);

                });
                packet.setData(players);
            }
        }
    }

    private boolean canSeeOtherType(PlayerType playerTypeA, PlayerType playerTypeB) {
        switch (playerTypeA) {
            case PLAYER:
                return playerTypeB == PlayerType.PLAYER;
            case SPECTATOR:
                return playerTypeB == PlayerType.PLAYER || playerTypeB == PlayerType.SPECTATOR;
            case MODERATOR:
                return playerTypeB == PlayerType.PLAYER || playerTypeB == PlayerType.SPECTATOR || playerTypeB == PlayerType.MODERATOR;
            case ADMINISTRATOR:
                return true;
            default:
                return false;
        }
    }
}
