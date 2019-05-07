package net.ml.unsafe.collections.concurrent;

import net.ml.unsafe.collections.list.MemoryBlockLinkedList;
import net.ml.unsafe.collections.memory.blocks.nodes.MemoryNode;
import net.ml.unsafe.collections.memory.blocks.ConcurrentMemoryBlock;

/**
 * Thread safe linkedlist
 *
 * @author micha
 * @param <T> the type to store in the linkedlist
 */
public class ConcurrentMemoryBlockLinkedList<T> extends MemoryBlockLinkedList<T> {
    public ConcurrentMemoryBlockLinkedList(ConcurrentMemoryBlock<MemoryNode<T>> memory) {
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
    }

    /**
     * Remove the element at the specified index
     *
     * @param index the index to remove
     * @return the removed element
     */
    @Override
    public T remove(int index) {
        return super.remove(index);
    }

    /**
     * Get the number of items in the linkedlist
     *
     * @return the number of items in the linkedlist
     */
    @Override
    public int size() {
        return super.size();
    }

    /**
     * Clear the contents of the list
     */
    @Override
    public void clear() {
        super.clear();
    }
}
