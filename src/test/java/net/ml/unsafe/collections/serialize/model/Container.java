package net.ml.unsafe.collections.serialize.model;

import java.io.Serializable;
import java.util.Objects;

public class Container<T> implements Serializable {
    private int x;
    public Short y;
    public T z;

    public Container() {}

    public Container(int x, short y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[x: " + x + ", y: " + y + ", z: " + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container<?> container = (Container<?>) o;
        return x == container.x &&
                y != null ? y.equals(container.y) : container.y != null &&
                z != null ? z.equals(container.z) : container.z != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
