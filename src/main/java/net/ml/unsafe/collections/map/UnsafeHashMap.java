package net.ml.unsafe.collections.map;

import net.ml.unsafe.collections.memory.MemoryBlock;

import java.util.AbstractMap;
import java.util.Set;

public class UnsafeHashMap<K, V> extends AbstractMap<K, V> {
    private final MemoryBlock memory;

    public UnsafeHashMap (MemoryBlock memory) {
        this.memory = memory;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }
}
