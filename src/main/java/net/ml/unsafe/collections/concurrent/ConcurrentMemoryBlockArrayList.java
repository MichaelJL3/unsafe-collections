package net.ml.unsafe.collections.concurrent;

import net.ml.unsafe.collections.list.MemoryBlockArrayList;
import net.ml.unsafe.collections.memory.ConcurrentMemoryBlock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread safe arrayList
 *
 * @author micha
 * @param <T> the type to store in the arraylist
 */
public class ConcurrentMemoryBlockArrayList<T> extends MemoryBlockArrayList<T> {
    private AtomicInteger size = new AtomicInteger(0);

    public ConcurrentMemoryBlockArrayList(ConcurrentMemoryBlock<T> memory) {
        super(memory);
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
        super.add(index, element);
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
        T o = super.remove(index);
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
        super.clear();
        size.set(0);
    }
}
