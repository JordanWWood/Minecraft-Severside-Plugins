package network.marble.dataaccesslayer.entities;

import lombok.Getter;
import lombok.Setter;

public class Vector {

    @Getter @Setter
    private double x;

    @Getter @Setter
    private double y;

    @Getter @Setter
    private double z;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public org.bukkit.util.Vector getBukkitVector() {
        return new org.bukkit.util.Vector(this.x, this.y, this.z);
    }
}
