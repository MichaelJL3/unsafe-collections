package net.ml.unsafe.collections.concurrent;

import net.ml.unsafe.collections.list.MemoryBlockLinkedList;
import net.ml.unsafe.collections.memory.blocks.ConcurrentMemoryBlock;

/**
 * Thread safe linkedlist
 *
 * @author micha
 * @param <T> the type to store in the linkedlist
 */
public class ConcurrentMemoryBlockLinkedList<T> extends MemoryBlockLinkedList<T> {
    public ConcurrentMemoryBlockLinkedList(ConcurrentMemoryBlock<T> memory) {
        super(memory);
    }
}
