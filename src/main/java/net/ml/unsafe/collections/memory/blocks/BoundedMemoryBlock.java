package net.ml.unsafe.collections.memory.blocks;

/**
 * Decorates memory blocks with safe index boundary checking
 *
 * @author micha
 * @param <T> the object type to manage in memory
 */
public final class BoundedMemoryBlock<T> implements MemoryBlock<T> {
    private final MemoryBlock<T> memory;

    /**
     * Constructor
     *
     * @param memory the memory block
     */
    public BoundedMemoryBlock(MemoryBlock<T> memory) {
        this.memory = memory;
    }

    /**
     * Allocate memory for n objects
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void malloc(int capacity) {
        memory.malloc(capacity);
    }

    /**
     * Increase memory allocation while preserving existing allocations data
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void realloc(int capacity) {
        memory.realloc(capacity);
    }

    /**
     * Get the object stored at the index from memory
     *
     * @param index the index in memory
     * @return the object retrieved
     *
     * @throws IndexOutOfBoundsException accessing memory outside the allocated block
     */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.get(index);
    }

    /**
     * Store the object in memory at the index
     *
     * @param index the index in the block to store
     * @param o the object to store
     *
     * @throws IndexOutOfBoundsException accessing memory outside the allocated block
     */
    @Override
    public void put(int index, T o) {
        if (additionOutOfBounds(index)) throw new IndexOutOfBoundsException();
        memory.put(index, o);
    }

    /**
     * Replace the object at the index
     *
     * @param index the index to replace
     * @param o the value to replace with
     * @return the replaced object
     *
     * @throws IndexOutOfBoundsException accessing memory outside the allocated block
     */
    @Override
    public T replace(int index, T o) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.replace(index, o);
    }

    /**
     * Remove the object at the index
     *
     * @param index the index to remove
     * @return the object removed
     *
     * @throws IndexOutOfBoundsException accessing memory outside the allocated block
     */
    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.remove(index);
    }

    /**
     * Release allocated memory
     */
    @Override
    public void free() {
        memory.free();
    }

    /**
     * Swap the objects at the two indexes in memory
     *
     * @param indexA the index of the first object
     * @param indexB the index of the second object
     */
    @Override
    public void swap(int indexA, int indexB) {
        memory.swap(indexA, indexB);
    }

    /**
     * Copy the object from one index in memory to another
     *
     * @param indexA the index of the object to copy
     * @param indexB the index to copy the object to
     */
    @Override
    public void copy(int indexA, int indexB) {
        memory.copy(indexA, indexB);
    }

    /**
     * Number of blocks allocated in memory
     *
     * @return the number of blocks
     */
    @Override
    public int size() {
        return memory.size();
    }

    /**
     * Check that the index is out of the memory bounds for addition
     *
     * @param index the index to validate
     * @return whether or not the index is out of bounds
     */
    private boolean additionOutOfBounds(int index) {
        return index > size() || index < 0;
    }

    /**
     * Check that the index is out of the memory bounds
     *
     * @param index the index to validate
     * @return whether or not the index is out of bounds
     */
    private boolean outOfBounds(int index) {
        return index >= size() || index < 0;
    }
}
