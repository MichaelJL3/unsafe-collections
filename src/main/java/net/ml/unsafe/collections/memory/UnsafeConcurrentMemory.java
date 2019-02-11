package net.ml.unsafe.collections.memory;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manage memory of objects through unsafe concurrently
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public class UnsafeConcurrentMemory<T extends Serializable> implements ConcurrentMemory<T> {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Memory<T> memory;

    /**
     * Create a new concurrent memory instance
     *
     * @param memory the memory to add concurrent protection to
     */
    public UnsafeConcurrentMemory(Memory<T> memory) {
        this.memory = memory;
    }

    /**
     * Allocate memory for n objects where n = capacity * sizeOf(T)
     *
     * @param capacity the number of objects to allocate for
     */
    @Override
    public void malloc(int capacity) {
        lock.writeLock().lock();
        memory.malloc(capacity);
        lock.writeLock().unlock();
    }

    /**
     * Reallocate memory to new location with increased size
     *
     * @param capacity the number of objects to reallocate for
     */
    @Override
    public void realloc(int capacity) {
        lock.writeLock().lock();
        memory.realloc(capacity);
        lock.writeLock().unlock();
    }

    /**
     * Free the allocated memory
     */
    @Override
    public void free() {
        lock.writeLock().lock();
        memory.free();
        lock.writeLock().unlock();
    }

    /**
     * Get the object at the given index
     *
     * @param index the index to retrieve
     * @return the retrieved object
     */
    @Override
    public T get(int index) {
        lock.readLock().lock();
        T object = memory.get(index);
        lock.readLock().unlock();
        return object;
    }

    /**
     * Put an object into the memory at the given block id
     *
     * @param index the index to store the object
     * @param o the object to store
     */
    @Override
    public void put(int index, T o) {
        lock.writeLock().lock();
        memory.put(index, o);
        lock.writeLock().unlock();
    }

    /**
     * Swap the memory blocks
     *
     * @param indexA the first index
     * @param indexB the second index
     */
    @Override
    public void swap(int indexA, int indexB) {
        lock.writeLock().lock();
        memory.swap(indexA, indexB);
        lock.writeLock().unlock();
    }

    /**
     * Copy a memory block to another block
     *
     * @param indexA the source index
     * @param indexB the destination index
     */
    @Override
    public void copy(int indexA, int indexB) {
        lock.writeLock().lock();
        memory.copy(indexA, indexB);
        lock.writeLock().unlock();
    }

    /**
     * Get the number of object blocks allocated
     *
     * @return the number of object blocks the memory holds
     */
    @Override
    public int size() {
        lock.readLock().lock();
        int size = memory.size();
        lock.readLock().unlock();
        return size;
    }

    /**
     * Copy the memory into a new memory object
     *
     * @return the unsafe memory copy
     */
    @Override
    public UnsafeConcurrentMemory<T> clone() {
        lock.writeLock().lock();
        UnsafeConcurrentMemory<T> memClone = new UnsafeConcurrentMemory<>(((UnsafeMemory<T>) memory).clone());
        lock.writeLock().unlock();
        return  memClone;
    }
}
