package net.ml.unsafe.collections.memory.blocks;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Memory block interface
 *
 * Manages a chunk of memory as blocks of objects
 *
 * @author micha
 * @param <T> the object type to manage in memory
 */
public interface MemoryBlock<T> extends Iterable<T>, AutoCloseable {
    /**
     * Allocate memory for n objects
     *
     * @param capacity the number of objects to allocate memory for
     */
    void malloc(int capacity);

    /**
     * Increase memory allocation while preserving existing allocations data
     *
     * @param capacity the number of objects to allocate memory for
     */
    void realloc(int capacity);

    /**
     * Get the object stored at the index from memory
     *
     * @param index the index in memory
     * @return the object retrieved
     */
    T get(int index);

    /**
     * Store the object in memory at the index
     *
     * @param index the index in the block to store
     * @param o the object to store
     */
    void put(int index, T o);

    /**
     * Replace the object at the index
     *
     * @param index the index to replace
     * @param o the value to replace with
     * @return the replaced object
     */
    T replace(int index, T o);

    /**
     * Remove the object at the index
     *
     * @param index the index to remove
     * @return the removed object
     */
    T remove(int index);

    /**
     * Release allocated memory
     */
    void free();

    /**
     * Swap the objects at the two indexes in memory
     *
     * @param indexA the index of the first object
     * @param indexB the index of the second object
     */
    void swap(int indexA, int indexB);

    /**
     * Copy the object from one index in memory to another
     *
     * @param indexA the index of the object to copy
     * @param indexB the index to copy the object to
     */
    void copy(int indexA, int indexB);

    /**
     * Number of blocks allocated in memory
     *
     * @return the number of blocks
     */
    int size();

    /**
     * Frees memory when used in try with resources
     */
    @Override
    default void close() {
        free();
    }

    /**
     * Retrieve an iterator for the memory block
     *
     * @return the memory iterator
     */
    @Override
    default Iterator<T> iterator() {
        return new MemoryBlockIterator<>(this);
    }

    /**
     * Perform an action for every object in the memory block
     *
     * @param action the action to perform
     */
    @Override
    default void forEach(Consumer<? super T> action) {
        iterator().forEachRemaining(action);
    }

    /**
     * Unimplemented
     *
     * @throws UnsupportedOperationException cannot spliterate
     */
    @Override
    default Spliterator<T> spliterator() {
        throw new UnsupportedOperationException();
    }
}
