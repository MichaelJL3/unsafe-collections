package net.ml.unsafe.collections.memory;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Iterate through a memory allocation
 *
 * @author micha
 * @param <T> the type of object stored
 */
public class MemoryIterator<T> implements Iterator<T> {
    private final Memory<T> memory;
    private int index = 0;

    /**
     * Create a new memory iterator
     *
     * @param memory the memory to iterate through
     */
    public MemoryIterator(Memory<T> memory) {
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
     * Does nothing the block can be overwritten if needed
     */
    @Override
    public void remove() {

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
