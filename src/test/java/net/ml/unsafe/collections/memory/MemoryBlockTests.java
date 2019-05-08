package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.memory.blocks.*;
import org.junit.Assert;
import org.junit.Test;

public class MemoryBlockTests {
    @Test
    public void swapArrayBlockTest() {
        swapTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
    }

    @Test
    public void copyArrayBlockTest() {
        copyTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
    }

    @Test
    public void storageArrayBlockTest() {
        storageTest(new ArrayMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void allocateArrayBlockTest() {
        allocationTest(new ArrayMemoryBlock<>(Integer.BYTES, 0));
    }

    @Test
    public void reallocateArrayBlockTest() {
        reallocationTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
    }

    @Test
    public void swapLinkedBlockTest() {
        swapTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void copyLinkedBlockTest() {
        copyTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void storageLinkedBlockTest() {
        storageTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void removeLinkedBlockTest() {
        removeTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void swapArrayReferenceBlockTest() {
        swapTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void copyArrayReferenceBlockTest() {
        copyTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void storageArrayReferenceBlockTest() {
        storageTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void allocateArrayReferenceBlockTest() {
        allocationTest(new ArrayReferenceMemoryBlock<>(0));
    }

    @Test
    public void reallocateArrayReferenceBlockTest() {
        reallocationTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void swapLinkedReferenceBlockTest() {
        swapTest(new LinkedReferenceMemoryBlock<>());
    }

    @Test
    public void copyLinkedReferenceBlockTest() {
        copyTest(new LinkedReferenceMemoryBlock<>());
    }

    @Test
    public void storageLinkedReferenceBlockTest() {
        storageTest(new LinkedReferenceMemoryBlock<>());
    }

    @Test
    public void removeLinkedReferenceBlockTest() {
        removeTest(new LinkedReferenceMemoryBlock<>());
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
