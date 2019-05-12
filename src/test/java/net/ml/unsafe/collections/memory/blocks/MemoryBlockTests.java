package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.SafeTest;
import org.junit.Assert;
import org.junit.Test;

public class MemoryBlockTests extends SafeTest {
    @Test
    public void swapArrayBlockTest() {
        swapTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void copyArrayBlockTest() {
        copyTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void storageArrayBlockTest() {
        storageTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeArrayBlockTest() {
        removeTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void allocateArrayBlockTest() {
        allocationTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(0).build());
    }

    @Test
    public void reallocateArrayBlockTest() {
        reallocationTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void swapLinkedBlockTest() {
        swapTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void copyLinkedBlockTest() {
        copyTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void storageLinkedBlockTest() {
        storageTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void removeLinkedBlockTest() {
        removeTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void allocateLinkedBlockTest() {
        allocationTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(0).build());
    }

    @Test
    public void reallocateLinkedBlockTest() {
        reallocationTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void swapArrayReferenceBlockTest() {
        swapTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void copyArrayReferenceBlockTest() {
        copyTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void storageArrayReferenceBlockTest() {
        storageTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeArrayReferenceBlockTest() {
        removeTest(ArrayReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void allocateArrayReferenceBlockTest() {
        allocationTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(0).build());
    }

    @Test
    public void reallocateArrayReferenceBlockTest() {
        reallocationTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void swapLinkedReferenceBlockTest() {
        swapTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void copyLinkedReferenceBlockTest() {
        copyTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void storageLinkedReferenceBlockTest() {
        storageTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void allocateLinkedReferenceBlockTest() {
        allocationTest(LinkedReferenceMemoryBlock.<Integer>builder().capacity(0).build());
    }

    @Test
    public void reallocateLinkedReferenceBlockTest() {
        reallocationTest(LinkedReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void removeLinkedReferenceBlockTest() {
        removeTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    private void allocationTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            int size = 10;
            memory.malloc(size);
            Assert.assertEquals(size, memory.size());

            memory.free();
            Assert.assertEquals(0, memory.size());
        }
    }

    private void reallocationTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            memory.put(0, 1);

            int size = 20;
            memory.realloc(size);
            Assert.assertEquals(size, memory.size());

            Assert.assertEquals(new Integer(1), memory.get(0));

            memory.free();
            Assert.assertEquals(0, memory.size());
        }
    }

    private void storageTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            Integer contentOne = 1;
            Integer contentTwo = 2;

            memory.put(0, contentOne);
            memory.put(1, contentTwo);

            Assert.assertEquals(contentOne, memory.get(0));
            Assert.assertEquals(contentTwo, memory.get(1));
        }
    }

    private void removeTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            Integer contentOne = 1;
            Integer contentTwo = 2;

            memory.put(0, contentOne);
            memory.put(1, contentTwo);

            Assert.assertEquals(contentTwo, memory.remove(1));
            Assert.assertEquals(contentOne, memory.remove(0));
        }
    }

    private void swapTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            Integer contentOne = 1;
            Integer contentTwo = 2;

            memory.put(0, contentOne);
            memory.put(1, contentTwo);
            memory.swap(0, 1);

            Assert.assertEquals(contentOne, memory.get(1));
            Assert.assertEquals(contentTwo, memory.get(0));
        }
    }

    private void copyTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = block) {
            Integer contentOne = 1;
            Integer contentTwo = 2;

            memory.put(0, contentOne);
            memory.put(1, contentTwo);
            memory.copy(0, 1);

            Assert.assertEquals(contentOne, memory.get(0));
            Assert.assertEquals(contentOne, memory.get(1));
        }
    }
}
