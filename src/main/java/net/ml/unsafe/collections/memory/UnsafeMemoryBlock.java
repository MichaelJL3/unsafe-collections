package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;
import net.ml.unsafe.collections.serialize.ByteSerializerType;
import net.ml.unsafe.collections.util.SizeUtil;
import net.ml.unsafe.collections.util.UnsafeSingleton;
import sun.misc.Unsafe;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Manage memory of objects through unsafe
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public final class UnsafeMemoryBlock<T> implements MemoryBlock<T> {
    private static final Unsafe unsafe = UnsafeSingleton.getUnsafe();

    private long address = -1;
    private int capacity;

    private final Memory memory = new UnsafeMemory();

    private final ByteSerializer<T> serializer;
    private final ByteSerializerType type;
    private final Class<T> classType;
    private final int classSize;

    public UnsafeMemoryBlock(Class<T> classType, int capacity) {
        this(classType, capacity, ByteSerializerType.DEFAULT);
    }

    /**
     * Create a new memory instance with enough memory to hold n objects
     * where n = capacity * sizeOf(T)
     *
     * @param classType the type of object to store
     */
    public UnsafeMemoryBlock(Class<T> classType, int capacity, ByteSerializerType type) {
        this.type = type;
        this.classType = classType;
        this.classSize = SizeUtil.sizeOf(classType);
        this.serializer = ByteSerializerFactory.getSerializer(type);
        malloc(capacity);
    }

    /**
     * Allocate memory for n objects where n = capacity * sizeOf(T)
     *
     * @param capacity the number of objects to allocate for
     */
    @Override
    public void malloc(int capacity) {
        //deallocate used memory
        if (capacity <= 0) throw new IllegalArgumentException();
        if (address != -1) free();

        address = memory.malloc(capacity * classSize);
        this.capacity = capacity;
    }

    /**
     * Reallocate memory to new location with increased size
     *
     * @param capacity the number of objects to reallocate for
     */
    @Override
    public void realloc(int  capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();

        address = (address == -1) ?
                memory.malloc(capacity * classSize) :
                memory.realloc(address, capacity * classSize);
        this.capacity = capacity;
    }

    /**
     * Free the allocated memory
     */
    @Override
    public void free() {
        memory.free(address);
        address = -1;
    }

    /**
     * Swap the objects in a memory block
     *
     * @param indexA the first index
     * @param indexB the second index
     *
     * @throws IndexOutOfBoundsException attempting to access memory outside the allocated region
     */
    @Override
    public void swap(int indexA, int indexB) {
        if (outOfBounds(indexA) || outOfBounds(indexB)) throw new IndexOutOfBoundsException();

        T tmp = get(indexB);
        put(indexB, get(indexA));
        put(indexA, tmp);
    }

    /**
     * Copy an object in memory to another segment of the memory
     *
     * @param indexA the index to copy
     * @param indexB the location to copy to
     *
     * @throws IndexOutOfBoundsException attempting to access memory outside the allocated region
     */
    @Override
    public void copy(int indexA, int indexB) {
        if (outOfBounds(indexA) || outOfBounds(indexB)) throw new IndexOutOfBoundsException();

        put(indexB, get(indexA));
    }

    /**
     * Get the object at the given index
     *
     * @param index the index to retrieve
     * @return the retrieved object
     *
     * @throws IndexOutOfBoundsException attempting to access memory outside the allocated region
     */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        byte[] bytes = memory.get(getMemoryAddress(index), classSize);
        return serializer.deserialize(bytes);
    }

    /**
     * Put an object into the memory at the given block id
     *
     * @param index the index to store the object
     * @param o the object to store
     */
    @Override
    public void put(int index, T o) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        memory.put(getMemoryAddress(index), serializer.serialize(o));
    }

    /**
     * Get the number of object blocks allocated
     *
     * @return the number of object blocks the memory holds
     */
    @Override
    public int size() {
        return capacity;
    }

    /**
     * Copy the memory into a new memory object
     *
     * @return the unsafe memory copy
     */
    @Override
    public UnsafeMemoryBlock<T> clone() {
        UnsafeMemoryBlock<T> memClone = new UnsafeMemoryBlock<>(classType, capacity, type);
        unsafe.copyMemory(address, memClone.address, capacity * classSize);

        return  memClone;
    }

    /**
     * Get the memory address of the block
     *
     * @param index the memory block
     * @return the memory address
     */
    private long getMemoryAddress(int index) {
        return address + index * classSize;
    }

    /**
     * Check that the index is out of the memory bounds
     *
     * @param index the index to validate
     * @return whether or not the index is out of bounds
     */
    private boolean outOfBounds(int index) {
        return index <= capacity && index >= 0;
    }

    /**
     * Retrieve an iterator for the unsafe memory block
     *
     * @return the memory iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new MemoryBlockIterator<>(this);
    }

    /**
     * Perform an action for every object in the memory block
     *
     * @param action the action to perform
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        iterator().forEachRemaining(action);
    }

    /**
     * Unimplemented
     *
     * @throws UnsupportedOperationException cannot spliterate
     */
    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException();
    }
}
