package net.ml.unsafe.collections.memory.blocks.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Node implementation which holds addresses for up to doubly linked elements
 *
 * @param <T> the type of object to store in the node
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleLinkedMemoryNode<T> implements MemoryNode<T> {
    private long addr;
    private long next;
    private T value;

    @Override
    public long getPrev() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPrev(long address) {
        throw new UnsupportedOperationException();
    }
}
