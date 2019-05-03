package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.UnsafeMemory;

import java.util.AbstractList;

/**
 * ArrayList using unsafe memory allocation
 *
 * @author micha
 * @param <T> the type to store in the arraylist
 */
public class UnsafeArrayList<T> extends AbstractList<T> {
    private static final int DEFAULT_CAPACITY = 16;

    private int size = 0;
    private final Memory memory;

    /**
     * Create a new unsafe arraylist
     *
     * @param classType the type of object stored
     */
    public UnsafeArrayList(Class<T> classType) {
        this(classType, DEFAULT_CAPACITY);
    }

    /**
     * Create a new unsafe arraylist with the given size
     *
     * @param classType the type of object stored
     * @param capacity initial capacity of the arraylist
     */
    public UnsafeArrayList(Class<T> classType, int capacity) {
        this.memory = new UnsafeMemory<>(classType, capacity);
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

        for (int i = size; i > index; --i) {
            memory.put(i, memory.get(i - 1));
        }

        memory.put(index, element);
        ++size;
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

        for (int i = index; i < size - 1; ++i) {
            memory.put(i, memory.get(i + 1));
        }

        --size;
        return o;
    }

    /**
     * Get the number of items in the arraylist
     *
     * @return the number of items in the arraylist
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Clear the contents of the list
     */
    @Override
    public void clear() {
        memory.free();
        size = 0;
    }

    /**
     * Check if the arraylist is full
     *
     * @return whether or not the arraylist is at capacity
     */
    private boolean isFull() {
        return memory.size() == size;
    }

    /**
     * Attampt to increase the size of the arraylist by 1.5 times the size
     */
    private void resize() {
        memory.realloc(size);
    }

    /**
     * Check that the index is out of the arraylist bounds
     *
     * @param index the index to validate
     * @return whether or not the index is out of bounds
     */
    private boolean outOfBounds(int index) {
        return index < 0 || index > size;
    }
}
