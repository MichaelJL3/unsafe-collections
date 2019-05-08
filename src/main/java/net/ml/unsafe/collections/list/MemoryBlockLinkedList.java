package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;

import java.util.AbstractList;

/**
 * MemoryBlockLinkedList using managed memory allocation
 *
 * @author micha
 * @param <T> the type to store in the linkedlist
 */
public class MemoryBlockLinkedList<T> extends AbstractList<T> {
    private final MemoryBlock<T> memory;

    /**
     * Constructor
     *
     * @param memory the memory block
     */
    public MemoryBlockLinkedList(MemoryBlock<T> memory) {
        this.memory = memory;
    }

    /**
     * Get the object at the index
     *
     * @param index the index to retrieve
     * @return the object retrieved
     */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.get(index);
    }

    /**
     * Set the object at the index
     *
     * @param index the index to set
     * @param element the object to set
     * @return the previous value at the index
     */
    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.replace(index, element);
    }

    /**
     * Add an object at the specified index
     *
     * @param index the index to add at
     * @param element the object to add
     */
    @Override
    public void add(int index, T element) {
        if (additionOutOfBounds(index)) throw new IndexOutOfBoundsException();
        memory.put(index, element);
    }

    /**
     * Remove the object at the index
     *
     * @param index the index to remove the object of
     * @return the removed object
     */
    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.remove(index);
    }

    /**
     * Get the number of elements in the list
     *
     * @return the size of the list
     */
    @Override
    public int size() {
        return memory.size();
    }

    /**
     * Remove all the objects from the list
     */
    @Override
    public void clear() {
        memory.free();
    }

    /**
     * Check that the index is within the lists bounds
     *
     * @param index the index to check
     * @return whether or not the index is within bounds
     */
    private boolean outOfBounds(int index) {
        return index >= memory.size() || index < 0;
    }

    /**
     * Check that the index is within the lists bounds
     * while allowing the next accessible index to be considered within bounds
     *
     * @param index the index to check
     * @return whether or not the index is within bounds
     */
    private boolean additionOutOfBounds(int index) {
        return index > memory.size() || index < 0;
    }
}
