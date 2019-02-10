package net.ml.unsafe.collections;

import com.sun.istack.internal.NotNull;
import net.ml.unsafe.collections.util.SizeOf;
import net.ml.unsafe.collections.util.UnsafeSerializer;
import net.ml.unsafe.collections.util.UnsafeSingleton;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Manage memory of objects through unsafe
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public final class UnsafeMemory<T> implements Memory<T>, Iterable<T> {
    private static final Unsafe unsafe = UnsafeSingleton.getUnsafe();

    private long address = -1;
    private int capacity;

    private final int classSize;
    private final Class<T> classType;

    /**
     * Create a new memory instance without any allocated space
     *
     * @param classType the type of object to store
     */
    public UnsafeMemory(Class<T> classType) {
        this.classType = classType;
        this.classSize = SizeOf.sizeOf(classType);
    }

    /**
     * Create a new memory instance with enough memory to hold n objects
     * where n = capacity * sizeOf(T)
     *
     * @param classType the type of object to store
     * @param capacity the number of objects to allocate for
     */
    public UnsafeMemory(Class<T> classType, int capacity) {
        this(classType);
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
        if (address != -1) free();

        address = unsafe.allocateMemory(capacity * classSize);;
        this.capacity = capacity;
    }

    /**
     * Reallocate memory to new location with increased size
     *
     * @param capacity the number of objects to reallocate for
     */
    @Override
    public void realloc(int  capacity) {
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
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return (T) UnsafeSerializer.load(classType, getMemoryAddress(index));
    }

    /**
     * Get the object at the given index
     *
     * Fills the provided object
     *
     * @param index the index to retrieve
     * @psram o the object to fill (should not be null - is unchecked)
     */
    @Override
    public void get(int index, @NotNull T o) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        UnsafeSerializer.fill(o, getMemoryAddress(index));
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
        UnsafeSerializer.store(o, getMemoryAddress(index));
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
        UnsafeMemory<T> memClone = new UnsafeMemory<>(classType, capacity);
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
