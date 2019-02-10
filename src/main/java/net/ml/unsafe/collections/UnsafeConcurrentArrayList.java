package net.ml.unsafe.collections;

import net.ml.unsafe.collections.memory.ConcurrentMemory;
import net.ml.unsafe.collections.memory.UnsafeConcurrentMemory;
import net.ml.unsafe.collections.memory.UnsafeMemory;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;
import net.ml.unsafe.collections.serialize.ByteSerializerType;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ArrayList using unsafe memory allocation
 *
 * @author micha
 * @param <T> the type to store in the arraylist
 */
public class UnsafeConcurrentArrayList<T extends Serializable> extends AbstractList<T> {
    private static final int DEFAULT_CAPACITY = 16;

    private AtomicInteger size = new AtomicInteger(0);
    private final ConcurrentMemory<T> memory;

    /**
     * Create a new unsafe arraylist
     */
    public UnsafeConcurrentArrayList() {
        this(ByteSerializerFactory.getSerializer(ByteSerializerType.DEFAULT), DEFAULT_CAPACITY);
    }

    /**
     * Create a new unsafe arraylist with the given serializer
     *
     * @param serializer the object serializer
     */
    public UnsafeConcurrentArrayList(ByteSerializer<T> serializer) {
        this(serializer, DEFAULT_CAPACITY);
    }

    /**
     * Create a new unsafe arraylist with the given size
     *
     * @param capacity initial capacity of the arraylist
     */
    public UnsafeConcurrentArrayList(int capacity) {
        this(ByteSerializerFactory.getSerializer(ByteSerializerType.DEFAULT), capacity);
    }

    /**
     * Create a new unsafe arraylist with the given size and serializer
     *
     * @param serializer the object serializer
     * @param capacity initial capacity of the arraylist
     */
    public UnsafeConcurrentArrayList(ByteSerializer<T> serializer, int capacity) {
        this.memory = new UnsafeConcurrentMemory<>(new UnsafeMemory<>(serializer, capacity));
    }

    /**
     * Get the object at the specified index
     *
     * @param index the index to retrieve
     * @return the object at the index
     */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.get(index);
    }

    /**
     * Set the object in the specified index of the list
     *
     * @param index the index to set the value of
     * @param element the element to set
     * @return the element replaced
     */
    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        T old = memory.get(index);
        memory.put(index, element);
        return old;
    }

    /**
     * Add a new element to the list
     *
     * Causes a shift of the element if not at the end
     *
     * @param index the index to insert at
     * @param element the element to insert
     */
    @Override
    public void add(int index, T element) {
        if (isFull()) resize();
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        for (int i = size.get(); i > index; --i) {
            memory.put(i, memory.get(i - 1));
        }

        memory.put(index, element);
        size.getAndIncrement();
    }

    /**
     * Remove the element at the specified index
     *
     * Causes the other elements to shift if not at the end
     *
     * @param index the index to remove
     * @return the removed element
     */
    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        T o = memory.get(index);
        int localSize = size.get();

        for (int i = index; i < localSize; ++i) {
            memory.put(i, memory.get(i + 1));
        }

        size.getAndDecrement();
        return o;
    }

    /**
     * Get the number of items in the arraylist
     *
     * @return the number of items in the arraylist
     */
    @Override
    public int size() {
        return size.get();
    }

    /**
     * Clear the contents of the list
     */
    @Override
    public void clear() {
        memory.free();
        size.set(0);
    }

    /**
     * Check if the arraylist is full
     *
     * @return whether or not the arraylist is at capacity
     */
    private boolean isFull() {
        return memory.size() == size.get();
    }

    /**
     * Attampt to increase the size of the arraylist by 1.5 times the size
     */
    private void resize() {
        memory.realloc(size.get());
    }

    /**
     * Check that the index is out of the arraylist bounds
     *
     * @param index the index to validate
     * @return whether or not the index is out of bounds
     */
    private boolean outOfBounds(int index) {
        return index < 0 || index > size.get();
    }
}
