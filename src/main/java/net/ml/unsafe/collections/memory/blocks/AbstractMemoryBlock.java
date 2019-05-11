package net.ml.unsafe.collections.memory.blocks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMemoryBlock<T> implements MemoryBlock<T>, Cloneable {
    public void copyFrom(MemoryBlock<T> memory) {
        int size = memory.size();

        for (int i = 0; i < size; ++i) {
            this.put(i, memory.get(i));
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected MemoryBlock<T> clone() throws CloneNotSupportedException {
        MemoryBlock<T> memory = null;

        try {
            Object clone = super.clone();

            if (clone instanceof MemoryBlock) {
                memory = (MemoryBlock<T>) clone;

                for (int i = 0; i < size(); ++i) {
                    memory.put(i, this.get(i));
                }
            }
        } catch(CloneNotSupportedException ex) {}

        return memory;
    }
}
