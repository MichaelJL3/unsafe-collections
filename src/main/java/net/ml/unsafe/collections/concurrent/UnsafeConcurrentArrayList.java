package net.ml.unsafe.collections.concurrent;

import net.ml.unsafe.collections.list.UnsafeArrayList;
import net.ml.unsafe.collections.memory.MemoryBlock;
import net.ml.unsafe.collections.memory.UnsafeConcurrentMemoryBlock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ArrayList using unsafe memory allocation
 *
 * @author micha
 * @param <T> the type to store in the arraylist
 */
public class UnsafeConcurrentArrayList<T> extends UnsafeArrayList<T> {
    private AtomicInteger size = new AtomicInteger(0);

    public UnsafeConcurrentArrayList(MemoryBlock<T> memory) {
        super(new UnsafeConcurrentMemoryBlock<>(memory));
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
