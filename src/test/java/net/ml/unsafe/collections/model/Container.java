package net.ml.unsafe.collections.model;

import java.io.Serializable;
import java.util.Objects;

public class Container<T> implements Serializable {
    private int x = 3;
    public Short y;
    public T z;

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
                y.equals(container.y) &&
                z.equals(container.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
