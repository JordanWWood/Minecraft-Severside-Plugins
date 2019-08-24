package network.marble.inventoryapi.itemstacks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.interfaces.ActionExecutor;
import network.marble.inventoryapi.interfaces.ItemStackGetter;

public class ActionItemStack extends InventoryItem{
	@Getter @Setter
	private ActionExecutor executor;
	@Getter @Setter
	private boolean doubleChecked; //Whether the InventoryItem should trigger a standard confirmation menu before continuing with its action
	@Getter
	private boolean closeOnExecute = false;
	@Getter @Setter
	private String[] executorArgs = null;
	
	public ActionItemStack(ItemStack itemStack, ActionExecutor executor, boolean requiresConfirmation) {
		super(itemStack);
		this.executor = executor;
		this.doubleChecked = requiresConfirmation;
	}
	
	public ActionItemStack(ItemStack itemStack, ActionExecutor executor, boolean requiresConfirmation, ItemStackGetter getter) {
		super(itemStack, getter);
		this.executor = executor;
		this.doubleChecked = requiresConfirmation;
	}
	
	public ActionItemStack(ItemStack itemStack, ActionExecutor executor, boolean requiresConfirmation, String[] args) {
		super(itemStack);
		this.executor = executor;
		this.doubleChecked = requiresConfirmation;
		this.executorArgs = args;
	}
	
	public ActionItemStack(ItemStack itemStack, ActionExecutor executor, boolean requiresConfirmation, ItemStackGetter getter, String[] executorArgs) {
		super(itemStack, getter);
		this.executor = executor;
		this.doubleChecked = requiresConfirmation;
		this.executorArgs = executorArgs;
	}
	
	public ActionItemStack setCloseOnExecute(boolean setting){
		this.closeOnExecute = setting;
		return this;
	}

	@Override
	public void execute(Player p, int slot) {
		if(executor != null){
			executor.executeAction(p, this, executorArgs);
		}
		if(closeOnExecute){
			p.closeInventory();
		}
		if(this.isUsesGetter()){
			InventoryAPI.refreshPlayerView(p);
		}
	}
}
