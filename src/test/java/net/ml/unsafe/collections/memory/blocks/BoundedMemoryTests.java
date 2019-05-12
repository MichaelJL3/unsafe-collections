package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.SafeTest;
import org.junit.Test;

public class BoundedMemoryTests extends SafeTest {
    @Test
    public void inBoundsArrayBlockTest() {
        inBoundsTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsArrayBlockTest() {
        outOfBoundsTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void inBoundsLinkedBlockTest() {
        inBoundsTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsLinkedBlockTest() {
        outOfBoundsTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void inBoundsArrayReferenceBlockTest() {
        inBoundsTest(ArrayReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsArrayReferenceBlockTest() {
        outOfBoundsTest(ArrayReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void inBoundsLinkedReferenceBlockTest() {
        inBoundsTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsLinkedReferenceBlockTest() {
        outOfBoundsTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    private void inBoundsTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = new BoundedMemoryBlock<>(block)) {
            memory.put(0, 1);
            memory.get(0);
        }
    }

    private void outOfBoundsTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = new BoundedMemoryBlock<>(block)) {
            memory.get(9);
        }
    }
}
