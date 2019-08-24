package network.marble.quickqueue.menus;

import java.util.ArrayList;

import network.marble.dataaccesslayer.models.Game;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.quickqueue.menus.getters.GameMenuIconGetter;
import network.marble.quickqueue.menus.getters.LangGetter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.api.SlotCollisionException;
import network.marble.inventoryapi.itemstacks.ActionItemStack;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.itemstacks.StandardItemStack;
import network.marble.inventoryapi.itemstacks.SubMenuInvokingItemStack;
import network.marble.quickqueue.GameSet;
import network.marble.quickqueue.QuickQueue;
import network.marble.quickqueue.actions.CreateParty;
import network.marble.quickqueue.actions.DisbandParty;
import network.marble.quickqueue.actions.JoinQueue;
import network.marble.quickqueue.actions.LeaveParty;
import network.marble.quickqueue.actions.ToggleMemberInvitePrivileges;
import network.marble.quickqueue.menus.executors.GameQueueDecisionExecutor;
import network.marble.quickqueue.menus.getters.GameQueueDecisionGetter;
import network.marble.quickqueue.menus.getters.InviteToggleGetter;
import network.marble.quickqueue.menus.executors.LeaderTransferLaunchExecutor;
import network.marble.quickqueue.menus.executors.ManageMembersListLaunchExecutor;
import network.marble.quickqueue.menus.executors.ViewInvitesLaunchExecutor;
import network.marble.quickqueue.menus.executors.ViewMembersLaunchExecutor;
import network.marble.quickqueue.menus.executors.PartyMenuLaunchExecutor;
import network.marble.quickqueue.menus.getters.ManagePartyGetter;

public class Menus {
    public static ItemStack disableInvitingIS;//Green potion to display that member inviting is enabled
    public static ItemStack enableInvitingIS;//Red potion to display that member inviting is disabled

    public static ItemStack joinQueueIS;
    public static ItemStack exitQueueIS;

    public static ItemStack managePartyIS;

    public static InventoryItem spacer;
    public static InventoryItem viewInvites;
    public static InventoryItem createParty;
    public static InventoryItem leaveParty;
    public static InventoryItem viewMembers;

    public static InventoryItem leaderOnly;
    public static InventoryItem inviteMember;

    public static InventoryItem disbandParty;
    public static InventoryItem manageMembers;
    public static InventoryItem transferLeader;
    public static InventoryItem toggleMemberInvitePrivileges;

    public static InventoryItem[] gameIcons;

    public static void buildInventoryMenus(){
        boolean hasPartialRow = QuickQueue.games.size() % 9 > 0;
        int size = (QuickQueue.games.size() / 9) * 9 + (hasPartialRow ? 9 : 0);
        if(size == 0) size = InventoryType.HOPPER.getDefaultSize();
        gameIcons = generateGameIconMenu(size, hasPartialRow);

        ////Hotbar Visible Items////
        //Join Game Queue Icon
        joinQueueIS = InventoryAPI.createItemStack(Material.COMPASS, 1, (short)0, ChatColor.GOLD +""+ ChatColor.BOLD + "Games", null);

        //Exit Game Queue Icon
        exitQueueIS = InventoryAPI.createItemStack(Material.TRAP_DOOR, 1, (short)0, ChatColor.GOLD +""+ ChatColor.BOLD + "Leave Queue", null);

        //Manage Party Icon
        managePartyIS = InventoryAPI.createItemStack(Material.NAME_TAG, 1, (short)0, ChatColor.GOLD +""+ ChatColor.BOLD + "Parties", null);

        ////Party Management Menu Items////
        //Spacer
        ItemStack spacerIS = InventoryAPI.createItemStack(Material.STAINED_GLASS_PANE, 1, (short)15, ChatColor.BLACK + " ", null);
        spacer = new ActionItemStack(spacerIS, null, false);

        //View Invites Icon
        ItemStack viewInvitesIS = InventoryAPI.createItemStack(Material.SKULL_ITEM, 1, (short)0, ChatColor.GOLD + "View Party Invites", null);
        viewInvites = new ActionItemStack(viewInvitesIS, new ViewInvitesLaunchExecutor(), false, new LangGetter(viewInvitesIS, "qq.menu.manage.party.view.invites", true));

        //Create Party Icon
        ItemStack createPartyIS = InventoryAPI.createItemStack(Material.ANVIL, 1, (short)0, ChatColor.GOLD + "Create A Party", null);
        createParty = new ActionItemStack(createPartyIS, CreateParty.getInstance(), false, new LangGetter(createPartyIS, "qq.menu.manage.party.create.party", true));

        //Leave Current Party Icon
        ItemStack leavePartyIS = InventoryAPI.createItemStack(Material.DARK_OAK_DOOR_ITEM, 1, (short)0, ChatColor.GOLD + "Leave Party", null);
        leaveParty = new ActionItemStack(leavePartyIS, new LeaveParty(), false, new LangGetter(leavePartyIS, "qq.menu.manage.party.leave.party", true));

        //View Party Members Icon
        ItemStack viewMembersIS = InventoryAPI.createItemStack(Material.SKULL_ITEM, 1, (short)0, ChatColor.GOLD + "View Party Members", null);
        viewMembers = new ActionItemStack(viewMembersIS, new ViewMembersLaunchExecutor(false), false, new LangGetter(viewInvitesIS, "qq.menu.manage.party.members.view", true));

        ////Member Inviting Minimum////
        //Send Party Invite Icon
        ItemStack invitePlayerIS = InventoryAPI.createItemStack(Material.BANNER, 1, (short)0, ChatColor.GOLD + ""+ChatColor.BOLD+"Invite Players", null);
        inviteMember = new SearchingMenuInvokingItemStack(invitePlayerIS, 3);//TODO lang

        //Leader Inviting Only
        ArrayList<String> leaderOnlyDesc = new ArrayList<>();
        leaderOnlyDesc.add(ChatColor.WHITE + "Your party leader must enable");
        leaderOnlyDesc.add(ChatColor.WHITE + "member invite privileges before you");
        leaderOnlyDesc.add(ChatColor.WHITE + "can invite others to your party.");
        ItemStack leaderOnlyIS = InventoryAPI.createItemStack(Material.BARRIER, 1, (short)0, "Invite Players - Party leader only.", leaderOnlyDesc);
        leaderOnly = new StandardItemStack(leaderOnlyIS);

        //Disband Party Icon
        ItemStack disbandPartyIS = InventoryAPI.createItemStack(Material.TNT, 1, (short)0, ChatColor.GOLD + "Disband Party", null);
        disbandParty = new ActionItemStack(disbandPartyIS, DisbandParty.getInstance(), true, new LangGetter(disbandPartyIS, "qq.menu.manage.party.disband.party", true));

        //View Party Members Icon
        ItemStack manageMembersIS = InventoryAPI.createItemStack(Material.SKULL_ITEM, 1, (short)0, ChatColor.GOLD + "Manage Party Members", null);
        manageMembers = new ActionItemStack(manageMembersIS, new ManageMembersListLaunchExecutor(), false, new LangGetter(manageMembersIS, "qq.menu.manage.party.members.manage", true));

        //Transfer Party Leadership Icon
        ItemStack transferLeaderIS = InventoryAPI.createItemStack(Material.MINECART, 1, (short)0, ChatColor.GOLD + "Transfer Leadership", null);
        transferLeader = new ActionItemStack(transferLeaderIS, new LeaderTransferLaunchExecutor(), false);

        //Enable Member Inviting Privileges Icon
        enableInvitingIS = new ItemStack(Material.POTION, 1, (short)0);
        PotionMeta pMOff = (PotionMeta)enableInvitingIS.getItemMeta();
        //PotionData pDOff = new PotionData(PotionType.INSTANT_HEAL);//TODO fix
        //pMOff.setBasePotionData(pDOff);
        pMOff.setDisplayName(ChatColor.GOLD + "Enable Member Inviting");
        pMOff.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        enableInvitingIS.setItemMeta(pMOff);

        //Disable Member Inviting Privileges Icon
        disableInvitingIS = new ItemStack(Material.POTION, 1, (short)0);
        PotionMeta pMOn = (PotionMeta)disableInvitingIS.getItemMeta();
        //PotionData pDOn = new PotionData(PotionType.JUMP);//TODO fix
        //pMOn.setBasePotionData(pDOn);
        pMOn.setDisplayName(ChatColor.GOLD + "Disable Member Inviting");
        pMOn.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        disableInvitingIS.setItemMeta(pMOn);

        toggleMemberInvitePrivileges = new ActionItemStack(enableInvitingIS, new ToggleMemberInvitePrivileges(), false, new InviteToggleGetter());

        try {
            InventoryAPI.addGlobalInventoryItem(new ActionItemStack(managePartyIS, new PartyMenuLaunchExecutor(), false, new ManagePartyGetter()), 8, 4);
            SubMenuInvokingItemStack queueSMIIS;
            if(gameIcons.length == 5) queueSMIIS = new SubMenuInvokingItemStack(joinQueueIS, InventoryType.HOPPER, gameIcons, "Select A Game:");
            else queueSMIIS = new SubMenuInvokingItemStack(joinQueueIS, gameIcons.length, gameIcons, "Select A Game:");
            InventoryAPI.addGlobalInventoryItem(new ActionItemStack(joinQueueIS, new GameQueueDecisionExecutor(queueSMIIS), false, new GameQueueDecisionGetter()), 1, 4);//TODO swap into x5
        } catch (SlotCollisionException e) {
            e.printStackTrace();
        }
    }

    private static InventoryItem[] generateGameIconMenu(int size, boolean hasPartialRow) {
        InventoryItem[] icons = new InventoryItem[size];
        if(QuickQueue.games != null){
            int i = 0;
            boolean emptyRowCountEven = hasPartialRow && (QuickQueue.games.size() % 9) % 2 == 0;
            boolean jumpedForCentering = false;
            boolean centerSlotSkipPerformed = false;
            int slotsUntilSkip = -1;

            //Build the Game/GameMode icons
            for(GameSet gm : QuickQueue.games.values()){
                //Additional positioning calculation/centering for the last row
                if(hasPartialRow && i >= size - 9){
                    if(!jumpedForCentering){
                        //Calculate the slots to jump from the left if a row isn't full of games
                        int jumpSlots = (9 - (QuickQueue.games.size() % 9 + (emptyRowCountEven ? 1 : 0))) / 2;//Adds one to account for blank middle space, used to know how far to skip i forward for centering
                        i += jumpSlots;
                        //Calculates amount of slots until a middle skip and subtracts one for the game about to be processed
                        if(emptyRowCountEven) slotsUntilSkip = (9 - jumpSlots * 2) / 2 - 1;
                        jumpedForCentering = true;
                    }else{
                        if(emptyRowCountEven && !centerSlotSkipPerformed){//Should skip once for empty slot?
                            if(slotsUntilSkip == 0){//Skip middle slot
                                i++;
                                centerSlotSkipPerformed = true;
                            }else{//Decrement towards a skip
                                slotsUntilSkip--;
                            }
                        }
                    }
                }

                Game game = gm.getGame();
                GameMenuIconGetter gameIcon = new GameMenuIconGetter(game.getName(), game.getDescription(), game.getIconMap());
                int modeCount = gm.getModes().size();
                if(modeCount > 1){
                    InventoryItem[] modeIcons = new InventoryItem[modeCount + (9-(modeCount%9))];
                    for(int j = 0; j < modeCount; j++){
                        GameMode gamemode = gm.getModes().get(j);
                        GameMenuIconGetter gamemodeIcon = new GameMenuIconGetter(gamemode.getName(), gamemode.getDescription(), gamemode.getIconMap());
                        modeIcons[j] = new ActionItemStack(gameIcon.getDefaultIcon(), new JoinQueue(), false, gamemodeIcon,
                                new String[]{gamemode.id.toString()}).setCloseOnExecute(true);
                    }
                    icons[i] = new LangSubMenuInvokingItemStack(gameIcon.getDefaultIcon(), modeIcons.length, modeIcons, game.getName(), gameIcon);
                    //TODO multiple modes need to be generated
                }else{
                    icons[i] = new ActionItemStack(gameIcon.getDefaultIcon(), new JoinQueue(), false, gameIcon,
                            new String[]{gm.getModes().get(0).id.toString()}).setCloseOnExecute(true);
                }

                i++;
            }
        }

        return icons;
    }
}
