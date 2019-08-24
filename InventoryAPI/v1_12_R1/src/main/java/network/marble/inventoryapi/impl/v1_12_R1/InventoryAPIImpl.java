package network.marble.inventoryapi.impl.v1_12_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import network.marble.inventoryapi.InventoryAPIPlugin;
import network.marble.inventoryapi.api.InventoryAPI;
import network.marble.inventoryapi.itemstacks.InventoryItem;
import network.marble.inventoryapi.utils.Reflections;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Base64;
import java.util.UUID;

public class InventoryAPIImpl extends InventoryAPI {
    /***
     * Assigns an NBT texture to a skull ItemStack
     *
     * @param url The uuid of the owning player
     * @return A textured skull
     */
	public ItemStack setSkullTextureImpl(String url) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		propertyMap.put("textures", new Property("textures", new String(encodedData)));
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headMeta = head.getItemMeta();
		Reflections.getField(headMeta.getClass(), "profile", GameProfile.class).set(headMeta, profile);
		head.setItemMeta(headMeta);
		return head;
	}

	public ItemStack mergeItemMetaImpl(ItemStack item, boolean hideData) {
		ItemMeta im = item.getItemMeta().clone();
		im.setUnbreakable(hideData);
		if (hideData) {
			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			im.addItemFlags(ItemFlag.HIDE_DESTROYS);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		item.setItemMeta(im);
		return item;
	}

	public void refreshPlayerOffHandItemImpl(Player player){
		if(player != null){
			InventoryItem item = InventoryAPIPlugin.globalOffHandItem;

			InventoryItem groupItem = InventoryAPIPlugin.inventoryOffHandItems.get(getPlayerInventoryID(player.getUniqueId()));
			if(groupItem != null) item = groupItem;

			//TODO logic for different visibility levels of off-hand items
			if(item != null)player.getInventory().setItemInOffHand(item.getItemStack(player));
		}
	}
}
