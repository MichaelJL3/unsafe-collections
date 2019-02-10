package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.util.UnsafeSingleton;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Manage memory of objects through unsafe
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public class UnsafeMemory<T extends Serializable> implements Memory<T>, Iterable<T>, Cloneable {
    private static final Unsafe unsafe = UnsafeSingleton.getUnsafe();

    private long address = -1;
    private int capacity;
    private boolean hasWritten;
    private int classSize;

    private final ByteSerializer<T> serializer;

    /**
     * Create a new memory instancee
     *
     * @param serializer the byte serializer for the object
     * @param capacity the capacity of the memory store
     */
    public UnsafeMemory(ByteSerializer<T> serializer, int capacity) {
        this.serializer = serializer;
        this.capacity = capacity;
    }

    /**
     * Allocate memory for n objects where n = capacity * sizeOf(T)
     *
     * @param capacity the number of objects to allocate for
     */
    @Override
    public void malloc(int capacity) {
        //deallocate used memory
        if (address != -1) free();

        address = unsafe.allocateMemory(capacity * classSize);
        this.capacity = capacity;
    }

    /**
     * Reallocate memory to new location with increased size
     *
     * @param capacity the number of objects to reallocate for
     */
    @Override
    public void realloc(int capacity) {
        address = (address == -1) ?
                unsafe.allocateMemory(capacity * classSize) :
                unsafe.reallocateMemory(address, capacity* classSize);
        this.capacity = capacity;
    }

    /**
     * Free the allocated memory
     */
    @Override
    public void free() {
        unsafe.freeMemory(address);
        address = -1;
    }

    /**
     * Get the object at the given index
     *
     * @param index the index to retrieve
     * @return the retrieved object
     */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        if (!hasWritten) throw new IndexOutOfBoundsException("Cannot pull from memory without an initial write");

        byte[] bytes = new byte[classSize];
        loadBytes(bytes, getMemoryAddress(index));
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
        byte[] bytes = serializer.serialize(o);

        if (!hasWritten) {
            classSize = bytes.length;
            malloc(this.capacity);
            hasWritten = true;
        }

        storeBytes(bytes, getMemoryAddress(index));
    }

    /**
     * Swap the memory blocks
     *
     * @param indexA the first index
     * @param indexB the second index
     */
    @Override
    public void swap(int indexA, int indexB) {
        if (outOfBounds(indexA) || outOfBounds(indexB)) throw new IndexOutOfBoundsException();

        T tmp = get(indexB);
        put(indexB, get(indexA));
        put(indexA, tmp);
    }

    /**
     * Copy a memory block to another block
     *
     * @param indexA the source index
     * @param indexB the destination index
     */
    @Override
    public void copy(int indexA, int indexB) {
        if (outOfBounds(indexA) || outOfBounds(indexB)) throw new IndexOutOfBoundsException();
        put(indexB, get(indexA));
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
    public UnsafeMemory<T> clone() {
        UnsafeMemory<T> memClone = new UnsafeMemory<>(serializer, capacity);
        unsafe.copyMemory(address, memClone.address, capacity * classSize);

        return  memClone;
    }

    /**
     * Store bytes in unsafe memory
     *
     * @param bytes the bytes to store
     * @param address the address to store
     */
    private void storeBytes(byte[] bytes, long address) {
        unsafe.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, bytes.length);
    }

    /**
     * Load the objects bytes
     *
     * @param bytes the bytes to store
     * @param address the address to load
     */
    private void loadBytes(byte[] bytes, long address) {
        unsafe.copyMemory(null, address, bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, bytes.length);
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
        return index >= capacity || index < 0;
    }

    /**
     * Retrieve an iterator for the unsafe memory block
     *
     * @return the memory iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new MemoryIterator<>(this);
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
     * @return null
     */
    @Override
    public Spliterator<T> spliterator() {
        return null;
    }
}
