package net.ml.unsafe.collections.memory.blocks.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryLinkedNode<T> implements MemoryNode<T> {
    private long prev;
    private long addr;
    private long next;
    private T value;
}
