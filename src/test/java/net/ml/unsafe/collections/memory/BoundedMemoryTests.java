package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.memory.blocks.BoundedMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryReferenceBlock;
import net.ml.unsafe.collections.model.Container;
import org.junit.Test;

public class BoundedMemoryTests {
    @Test
    public void inBoundsTest() {
        int size = 1;

        try (MemoryBlock<Container<Integer>> block = new MemoryReferenceBlock<>(size);
             MemoryBlock<Container<Integer>> memory = new BoundedMemoryBlock<>(block)) {
            memory.put(0, new Container<>());
            memory.get(0);
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsTest() {
        int size = 1;

        try (MemoryBlock<Container<Integer>> block = new MemoryReferenceBlock<>(size);
             MemoryBlock<Container<Integer>> memory = new BoundedMemoryBlock<>(block)) {
            memory.get(3);
        }
    }
}
