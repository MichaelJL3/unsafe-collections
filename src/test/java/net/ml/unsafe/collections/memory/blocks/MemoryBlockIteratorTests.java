package net.ml.unsafe.collections.memory.blocks;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MemoryBlockIteratorTests {
    @Test
    public void iterateArrayMemoryBlock() {
        iterateTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void iteratePartialFilledArrayMemoryBlock() {
        iteratePartialFilledTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void iterateLinkedMemoryBlock() {
        iterateTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void iterateArrayReferenceMemoryBlock() {
        iterateTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void iteratePartialFilledArrayReferenceMemoryBlock() {
        iteratePartialFilledTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void iterateLinkedReferenceMemoryBlock() {
        iterateTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    private void iterateTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            memory.put(0, 1);
            memory.put(1, 2);
            memory.put(2, 3);
            memory.forEach(i -> log.info("{}", i));
        }
    }

    private void iteratePartialFilledTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            memory.put(0, 1);
            memory.forEach(i -> log.info("{}", i));
        }
    }
}
