package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;

/**
 * Manages a chunk of memory as blocks of objects
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public final class ArrayMemoryBlock<T> implements MemoryBlock<T> {
    private static final int DEFAULT_INIT_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private final ByteSerializer<T> serializer;
    private final Memory memory;
    private final int classSize;

    private long address = -1;
    private int capacity;

    /**
     * Constructor
     * Uses default initial capacity
     * Uses byte serializer factory default serializer
     *
     * @param classSize number of bytes per object
     */
    public ArrayMemoryBlock(int classSize) {
        this(classSize, DEFAULT_INIT_CAPACITY);
    }

    /**
     * Constructor
     * Uses byte serializer factory default serializer
     * Uses memory factory default
     *
     * @param classSize number of bytes per object
     * @param capacity number of objects to initially allocate for
     */
    public ArrayMemoryBlock(int classSize, int capacity) {
        this(classSize, capacity, ByteSerializerFactory.getDefault(), MemoryFactory.getDefault());
    }

    /**
     * Constructor
     * Uses memory factory default
     *
     * @param classSize number of bytes per object
     * @param capacity number of objects to initially allocate for
     * @param serializer byte serializer
     */
    public ArrayMemoryBlock(int classSize, int capacity, ByteSerializer<T> serializer) {
        this(classSize, capacity, serializer, MemoryFactory.getDefault());
        malloc(capacity);
    }

    /**
     * Constructor
     *
     * @param classSize number of bytes per object
     * @param capacity number of objects to initially allocate for
     * @param serializer byte serializer
     * @param memory the memory wrapper
     */
    public ArrayMemoryBlock(int classSize, int capacity, ByteSerializer<T> serializer, Memory memory) {
        this.classSize = classSize;
        this.serializer = serializer;
        this.memory = memory;
        malloc(capacity);
    }

    /**
     * Allocate memory for n objects
     *
     * @param capacity the number of objects to allocate memory for
     *
     * @throws IllegalArgumentException attempting to allocate negative bytes
     * @throws OutOfMemoryError attempting to allocate more memory then possible
     */
    @Override
    public void malloc(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException();
        if (capacity > MAXIMUM_CAPACITY) throw new OutOfMemoryError();

        //deallocate memory if used
        if (address != -1) free();

        address = memory.malloc(capacity * classSize);
        this.capacity = capacity;
    }

    /**
     * Increase memory allocation while preserving existing allocations data
     *
     * @param capacity the number of objects to allocate memory for
     *
     * @throws IllegalArgumentException attempting to allocate negative bytes
     * @throws OutOfMemoryError attempting to allocate more memory then possible
     */
    @Override
    public void realloc(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException();
        if (capacity > MAXIMUM_CAPACITY) throw new OutOfMemoryError();

        address = (address == -1) ?
                memory.malloc(capacity * classSize) :
                memory.realloc(address, this.capacity * classSize, capacity * classSize);
        this.capacity = capacity;
    }

    /**
     * Release allocated memory
     */
    @Override
    public void free() {
        if (address != -1) {
            memory.free(address);
            address = -1;
            capacity = 0;
        }
    }

    /**
     * Swap the objects at the two indexes in memory
     *
     * @param indexA the index of the first object
     * @param indexB the index of the second object
     */
    @Override
    public void swap(int indexA, int indexB) {
        T tmp = get(indexB);
        put(indexB, get(indexA));
        put(indexA, tmp);
    }

    /**
     * Copy the object from one index in memory to another
     *
     * @param indexA the index of the object to copy
     * @param indexB the index to copy the object to
     */
    @Override
    public void copy(int indexA, int indexB) {
        put(indexB, get(indexA));
    }

    /**
     * Get the object stored at the index from memory
     *
     * @param index the index in memory
     * @return the object retrieved
     */
    @Override
    public T get(int index) {
        byte[] bytes = memory.get(getMemoryAddress(index), classSize);
        return serializer.deserialize(bytes);
    }

    /**
     * Store the object in memory at the index
     *
     * @param index the index in the block to store
     * @param o the object to store
     */
    @Override
    public void put(int index, T o) {
        memory.put(getMemoryAddress(index), serializer.serialize(o));
    }

    /**
     * Replace the object at the index
     *
     * @param index the index to replace
     * @param o the value to replace with
     * @return the replaced object
     */
    @Override
    public T replace(int index, T o) {
        T old = get(index);
        put(index, o);
        return old;
    }

    /**
     * Remove the object at the index
     *
     * @param index the index to remove
     * @return null
     * @throws UnsupportedOperationException cannot remove from array
     */
    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Number of blocks allocated in memory
     *
     * @return the number of blocks
     */
    @Override
    public int size() {
        return capacity;
    }

    /**
     * Get the memory address of the object
     *
     * @param index the index of the object
     * @return the memory address of the object
     */
    private long getMemoryAddress(int index) {
        return address + index * classSize;
    }
}
