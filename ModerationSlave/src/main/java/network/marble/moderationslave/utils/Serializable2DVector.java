package network.marble.moderationslave.utils;

import lombok.Getter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Serializable2DVector {
    @Getter private int x,z;

    public Serializable2DVector(int x, int z){
        this.x = x;
        this.z = z;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(83, 109).append(x).append(z).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Serializable2DVector))
            return false;
        Serializable2DVector o = (Serializable2DVector) obj;
        if (x != o.x)
            return false;
        return z == o.z;
    }
}

