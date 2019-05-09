package net.ml.unsafe.collections.set;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MemoryBlockHashSet<T> extends AbstractSet<T> implements Set<T> {
    private static final Object PRESENT = new Object();

    private HashMap<T, Object> map;
    private final MemoryBlock<T> memory;

    public MemoryBlockHashSet(MemoryBlock<T> memory) {
        this.memory = memory;
    }

    @Override
    public Iterator<T> iterator() {
        return memory.iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(T o) {
        return map.put(o, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(0) == PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
