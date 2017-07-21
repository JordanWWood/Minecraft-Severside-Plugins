package network.marble.vanity.api;

import lombok.Getter;
import network.marble.inventoryapi.enums.ArmorType;
import network.marble.inventoryapi.inventories.ArmourSet;

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

    public static Slot getByArmourType(ArmorType at) {
        for(Slot v : values()){
            if(v.getArmorType() == at){
                return v;
            }
        }
        return null;
    }
}
