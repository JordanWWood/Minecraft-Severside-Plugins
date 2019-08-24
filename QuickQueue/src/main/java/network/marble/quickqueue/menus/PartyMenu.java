package network.marble.quickqueue.menus;

import network.marble.messagelibrary.api.Lang;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.managers.PartyManager;
import network.marble.quickqueue.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.inventories.Menu;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PartyMenu extends Menu{
    InventoryItem[] items = new InventoryItem[5];//TODO breaks bottom inv when open
    Player player;
    Inventory inv = null;
    boolean shown = false;
    BukkitTask loading = null;
    private Future<Party> partyFuture = null;
    private Party party = null;

    public PartyMenu(Player player, InventoryItem inventoryItem, int inventorySize) {
        super(player, inventoryItem, inventorySize);
        this.player = player;
        reloadMenu();
    }

    private void loadPartyData(){
        partyFuture = PartyManager.getInstance().getUserParty(player.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(QuickQueue.getInstance(), () -> {
            try {
                party = partyFuture.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                Lang.chat("qq.party.load.failed", player);
                if(stillOpen()) Bukkit.getScheduler().runTask(QuickQueue.getInstance(), player::closeInventory);
                return;
            }
            if(stillOpen()) Bukkit.getScheduler().runTask(QuickQueue.getInstance(), this::reloadMenu);
        });
    }

    public void reloadMenu(){//TODO redis conversion
        if(partyFuture == null || !partyFuture.isDone()) {
            loadPartyData();
        }else {
            for (int i = 0; i < items.length; i++)
                items[i] = Menus.spacer;//Initialise with all spacers to be overridden by icons

            if(party == null){//If not in party
                items[1] = Menus.createParty;
                items[3] = Menus.viewInvites;
            }else{//If in party
                if(party.getLeader().equals(player.getUniqueId())){//If leader of said party
                    items[0] = Menus.disbandParty;
                    items[2] = Menus.manageMembers;
                    //items[2] = Menus.toggleMemberInvitePrivileges;//TODO reevaluate using this
                    items[4] = Menus.inviteMember;
//                    items[4] = Menus.transferLeader;
                }else{
                    items[1] = Menus.leaveParty;
                    items[3] = Menus.viewMembers;
//                    items[4] = party.isMemberInvitingEnabled() ? Menus.inviteMember : Menus.leaderOnly;
                }
            }
            ItemStack[] itemsIS = new ItemStack[items.length];
            for (int i = 0; i < items.length; i++) itemsIS[i] = items[i].getItemStack(player);


            if(inv != null && stillOpen()){
                inv.setContents(itemsIS);
                player.getOpenInventory().getTopInventory().setContents(itemsIS);
                player.updateInventory();
            }else{
                inv = InventoryAPIPlugin.getPlugin().getServer().createInventory(null, InventoryType.HOPPER, Lang.get("qq.menu.manage.party.title", player));
                inv.setContents(itemsIS);
                player.openInventory(inv);
            }
        }
    }

    @Override
    public boolean execute(int slot, int rawSlot) {
        if(rawSlot >= 0 && rawSlot < 5){
            try{
                partyFuture = null;//Reset to allow reloading
                items[rawSlot].execute(player, rawSlot);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return slot != rawSlot;
    }

    private boolean stillOpen(){
        return InventoryAPI.getPlayerCurrentMenu(player.getUniqueId()) == this;
    }
}
