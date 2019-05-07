package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Iterate through a memory block allocation
 *
 * @author micha
 * @param <T> the type of object stored
 */
public final class MemoryBlockIterator<T> implements Iterator<T> {
    private final MemoryBlock<T> memory;
    private int index = 0;

    /**
     * Constructor
     *
     * @param memory the memory block to iterate through
     */
    public MemoryBlockIterator(MemoryBlock<T> memory) {
        this.memory = memory;
    }

    /**
     * Check if there are any more objects in memory
     *
     * @return whether or not there are more memory locations
     */
    @Override
    public boolean hasNext() {
        return memory.size() != index;
    }

    /**
     * Get the next object in the memory block
     *
     * @return the next object
     */
    @Override
    public T next() {
        return memory.get(index++);
    }

    /**
     * Remove the object from the memory block
     *
     * @throws UnsupportedOperationException cannot remove a block of the memory chunk
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Perform a given action on the remaining objects in the iterator
     *
     * @param action the action to perform
     */
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        while(hasNext()) action.accept(next());
    }
}
