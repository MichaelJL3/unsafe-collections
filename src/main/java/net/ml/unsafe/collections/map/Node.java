package net.ml.unsafe.collections.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
final class Node<K,V> implements Map.Entry<K,V> {
    private K key;
    private V value;

    @Override
    public final V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Map.Entry) {
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            return (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }
}
