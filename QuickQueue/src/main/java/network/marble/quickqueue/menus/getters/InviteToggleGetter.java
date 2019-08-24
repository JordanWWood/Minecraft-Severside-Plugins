package network.marble.quickqueue.menus.getters;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.quickqueue.menus.Menus;

public class InviteToggleGetter implements ItemStackGetter{

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
//        if(QueueAPI.isPlayerAPartyLeader(player.getUniqueId())){
//            Party party = QueueAPI.getMember(player.getUniqueId()).getParty();//TODO null checks
//            return party.isMemberInvitingEnabled() ? Menus.disableInvitingIS : Menus.enableInvitingIS;
//        }//TODO redis conversion
        return Menus.disableInvitingIS;
    }

}
