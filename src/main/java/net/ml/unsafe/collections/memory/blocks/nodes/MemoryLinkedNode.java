package net.ml.unsafe.collections.memory.blocks.nodes;

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
public class MemoryLinkedNode<T> implements MemoryNode<T> {
    private long prev;
    private long addr;
    private long next;
    private T value;
}
