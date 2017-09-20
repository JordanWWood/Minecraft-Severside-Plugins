package network.marble.vanity.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.enums.ArmorType;
import network.marble.vanity.Vanity;
import network.marble.vanity.managers.EquipmentManager;

public enum Slot {
	HEAD(ArmorType.HELMET, 0),
	CHEST(ArmorType.CHEST, 1),
	LEGS(ArmorType.LEGS, 2),
	BOOTS(ArmorType.BOOTS, 3),
	EFFECT(null, 4),
	TRAIL(null, 5),
	CROWN(null, 6),
	COMPANION(null, 7);

	@Getter private ArmorType armorType;
	@Getter private int value;

	Slot(ArmorType armorType, int value) {
		this.armorType = armorType;
		this.value = value;
	}

	public static Slot getByValue(int value) {
		for(Slot v : values()){
			if(v.getValue() == value){
				return v;
			}
		}
		return null;
	}

	public static Slot getByCategory(String category) {
		category = category.equals("Hat") ? "HEAD" : category;
		for(Slot v : values()){
			if(v.toString().contains(category.toUpperCase())) {
				return v;
			}
		}
		return null;
	}

	public static Slot getByArmourType(ArmorType at) {
		for(Slot v : values()){
			if(v.getArmorType() == at){
				return v;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param name name of vanity item
	 * @param itemStack itemstack to display (can shift in case of cycling)
	 * @param player player being equipped to
	 */
	public static void equipTo(String name, Player player) {
		if (!EquipmentManager.getPlayerEquipment().containsKey(player.getUniqueId())) {
			EquipmentManager.getPlayerEquipment().put(player.getUniqueId(), new HashMap<>());
			EquipmentManager.getPlayerEquipmentBySlot().put(player.getUniqueId(), new HashMap<>());
		}
		Map<String, ItemStack> equipment = EquipmentManager.getPlayerEquipment().get(player.getUniqueId());
		Map<Slot, ItemStack> equipmentSlot = EquipmentManager.getPlayerEquipmentBySlot().get(player.getUniqueId());
		Slot s = Vanity.getVanityPluginManager().getPlugins().get(name).getSlot();
		String replace = "";
		// Method to check if another vanity item is equipped in the slot, then replace it with the new item being added
		for(String str : equipment.keySet()) {
			if(str.equals(name)) continue;
			if(Vanity.getVanityPluginManager().getPlugins().get(str).getSlot().getValue() == s.getValue()) {
				replace = str;
			}
		}
		ItemStack itemStack = Vanity.getVanityPluginManager().getPlugins().get(name).getDisplayItem();
		if(!replace.equals("")) unequipFrom(replace, player);
		equipment.put(name, itemStack);
		equipmentSlot.put(s, itemStack);
		InventoryAPI.refreshPlayerArmour(player);
	}

	/**
	 * 
	 * @param name name of vanity item
	 * @param player player unequipping to
	 */
	public static void unequipFrom(String name, Player player) {
		Map<String, ItemStack> equipment = EquipmentManager.getPlayerEquipment().get(player.getUniqueId());
		Map<Slot, ItemStack> equipmentSlot = EquipmentManager.getPlayerEquipmentBySlot().get(player.getUniqueId());
		equipment.remove(name);
		equipmentSlot.remove(Vanity.getVanityPluginManager().getPlugins().get(name).getSlot());
		InventoryAPI.refreshPlayerArmour(player);
	}
}
