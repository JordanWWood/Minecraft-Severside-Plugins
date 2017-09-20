package network.marble.vanity.menus.getters;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import network.marble.inventoryapi.interfaces.ItemStackGetter;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.vanity.api.Slot;
import network.marble.vanity.managers.EquipmentManager;

/**
 * Created by jorda_000 on 22/06/2017.
 */
public class WornItemGetter implements ItemStackGetter {

	private Slot slot;
	public	 WornItemGetter(Slot slot) {
		this.slot = slot;
	}

	@Override
	public ItemStack getItemStack(InventoryItem inventoryItem, Player player) {
		if (!EquipmentManager.getPlayerEquipment().containsKey(player.getUniqueId())) return new ItemStack(Material.AIR);
		if (!EquipmentManager.getPlayerEquipmentBySlot().get(player.getUniqueId()).containsKey(slot)) return new ItemStack(Material.AIR);

		ItemStack stack = EquipmentManager.getPlayerEquipmentBySlot().get(player.getUniqueId()).get(slot);
		return stack;
	}
}
