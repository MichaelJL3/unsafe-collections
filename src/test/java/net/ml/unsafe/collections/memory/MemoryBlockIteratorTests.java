package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryLinkedReferenceBlock;
import net.ml.unsafe.collections.model.Container;
import org.junit.Test;

public class MemoryBlockIteratorTests {
    @Test
    public void iterateTest() {
        int size = 3;

        try (MemoryBlock<Container<Integer>> memory = new MemoryLinkedReferenceBlock<>(size)) {
            memory.put(0, new Container<>(1, (short) 2, 3));
            memory.put(1, new Container<>(4, (short) 5, 6));
            memory.put(2, new Container<>(7, (short) 8, 9));

            memory.forEach(System.out::println);
        }
    }

    @Test(expected = Exception.class)
    public void iteratePartialFilledTest() {
        int size = 3;

        try (MemoryBlock<Integer> memory = new MemoryLinkedReferenceBlock<>(size)) {
            memory.put(0, 1);
            memory.forEach(System.out::println);
        }
    }
}
