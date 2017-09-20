package network.marble.vanity.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import network.marble.vanity.api.Slot;

public class EquipmentManager {
    @Getter static HashMap<UUID, Map<Slot, ItemStack>> playerEquipmentBySlot = new HashMap<>();
    @Getter static HashMap<UUID, Map<String, ItemStack>> playerEquipment = new HashMap<>();

    public EquipmentManager() {}
}