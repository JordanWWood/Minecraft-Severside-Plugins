package network.marble.quickqueue.menus.getters;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.quickqueue.menus.Menus;

public class MemberInviteGetter implements ItemStackGetter{

    @Override
    public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
//        if(QueueAPI.isPlayerAPartyLeader(player.getUniqueId())){
//            Party party = QuickQueue.parties.get(QuickQueue.partyMembers.get(player.getUniqueId()));//TODO null checks
//            return party.isMemberInvitingEnabled() ? Menus.disableInvitingIS : Menus.enableInvitingIS;
//        }//TODO convert to redis
        return Menus.disableInvitingIS;
    }

}
