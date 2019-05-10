package net.ml.unsafe.collections.memory.blocks;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages chunks of memory with thread safe access
 *
 * @author micha
 * @param <T> the object type to manage in memory
 */
public final class ReadWriteLockMemoryBlock<T> extends AbstractMemoryBlock<T> implements ConcurrentMemoryBlock<T> {
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
        try {
            memory.malloc(capacity);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Increase memory allocation while preserving existing allocations data using write lock
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void realloc(int capacity) {
        lock.writeLock().lock();
        try {
            memory.realloc(capacity);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Free the allocated memory using write lock
     */
    @Override
    public void free() {
        lock.writeLock().lock();
        try {
            memory.free();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get the object stored at the index from memory using read lock
     *
     * @param index the index in memory
     * @return the object retrieved
     */
    @Override
    public T get(int index) {
        T object;

        lock.readLock().lock();
        try {
            object = memory.get(index);
        } finally {
            lock.readLock().unlock();
        }

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
        try {
            memory.put(index, o);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Replace object at the index
     *
     * @param index the index to replace
     * @param o the value to replace with
     * @return the replaced object
     */
    @Override
    public T replace(int index, T o) {
        T old;

        lock.writeLock().lock();
        try {
            old = memory.replace(index, o);
        } finally {
            lock.writeLock().unlock();
        }

        return old;
    }

    /**
     * Remove the object at the index
     *
     * @param index the index to remove
     * @return the removed object
     */
    @Override
    public T remove(int index) {
        T old;

        lock.writeLock().lock();
        try {
            old = memory.remove(index);
        } finally {
            lock.writeLock().unlock();
        }

        return old;
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
        int size;

        lock.readLock().lock();
        try {
            size = memory.size();
        } finally {
            lock.readLock().unlock();
        }

        return size;
    }
}
