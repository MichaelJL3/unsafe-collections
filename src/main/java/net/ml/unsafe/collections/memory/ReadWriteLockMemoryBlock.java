package net.ml.unsafe.collections.memory;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages chunks of memory with thread safe access
 *
 * @author micha
 * @param <T> the object type to manage in memory
 */
public final class ReadWriteLockMemoryBlock<T> implements ConcurrentMemoryBlock<T> {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final MemoryBlock<T> memory;

    /**
     * Constructor
     *
     * @param memory the memory block
     */
    public ReadWriteLockMemoryBlock(MemoryBlock<T> memory) {
        this.memory = memory;
    }

    /**
     * Allocate memory for n objects using write lock
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void malloc(int capacity) {
        lock.writeLock().lock();
        memory.malloc(capacity);
        lock.writeLock().unlock();
    }

    /**
     * Increase memory allocation while preserving existing allocations data using write lock
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void realloc(int capacity) {
        lock.writeLock().lock();
        memory.realloc(capacity);
        lock.writeLock().unlock();
    }

    /**
     * Free the allocated memory using write lock
     */
    @Override
    public void free() {
        lock.writeLock().lock();
        memory.free();
        lock.writeLock().unlock();
    }

    /**
     * Get the object stored at the index from memory using read lock
     *
     * @param index the index in memory
     * @return the object retrieved
     */
    @Override
    public T get(int index) {
        lock.readLock().lock();
        T object = memory.get(index);
        lock.readLock().unlock();
        return object;
    }

    /**
     * Store the object in memory at the index using write lock
     *
     * @param index the index in the block to store
     * @param o the object to store
     */
    @Override
    public void put(int index, T o) {
        lock.writeLock().lock();
        memory.put(index, o);
        lock.writeLock().unlock();
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
     * Number of blocks allocated in memory using read lock
     *
     * @return the number of blocks
     */
    @Override
    public int size() {
        lock.readLock().lock();
        int size = memory.size();
        lock.readLock().unlock();
        return size;
    }
}
