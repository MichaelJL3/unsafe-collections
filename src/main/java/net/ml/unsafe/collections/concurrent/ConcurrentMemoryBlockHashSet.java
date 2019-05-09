package net.ml.unsafe.collections.concurrent;

import net.ml.unsafe.collections.memory.blocks.ConcurrentMemoryBlock;
import net.ml.unsafe.collections.set.MemoryBlockHashSet;

/**
 * Thread safe hash set
 *
 * @author micha
 * @param <T> the type to store in the hashset
 */
public class ConcurrentMemoryBlockHashSet<T> extends MemoryBlockHashSet<T> {
    public ConcurrentMemoryBlockHashSet(ConcurrentMemoryBlock<T> memory) {
        super(memory);
    }
}
