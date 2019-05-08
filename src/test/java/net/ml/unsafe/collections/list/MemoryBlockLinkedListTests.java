package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import net.ml.unsafe.collections.memory.blocks.SingleLinkedMemoryBlock;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemoryBlockLinkedListTests {
    @Test
    public void addToEndTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            List<Integer> list = new MemoryBlockLinkedList<>(memory);
            list.add(0);
            list.add(1);
            list.add(2);

            Assert.assertEquals(3, list.size());
            Assert.assertEquals(Arrays.asList(0, 1, 2), list);
        }
    }

    @Test
    public void insertInMiddleTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            List<Integer> list = new MemoryBlockLinkedList<>(memory);
            list.add(0);
            list.add(2);
            list.add(1, 1);

            Assert.assertEquals(new Integer(1), list.get(1));
            Assert.assertEquals(3, list.size());
            Assert.assertEquals(Arrays.asList(0, 1, 2), list);
        }
    }

    @Test
    public void removeFromEndTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            List<Integer> list = new MemoryBlockLinkedList<>(memory);
            list.add(0);
            list.add(2);
            list.remove(1);

            Assert.assertEquals(1, list.size());
            Assert.assertEquals(Collections.singletonList(0), list);
        }
    }

    @Test
    public void removeFromMiddleTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            List<Integer> list = new MemoryBlockLinkedList<>(memory);
            list.add(0);
            list.add(1);
            list.add(2);
            list.remove(1);

            Assert.assertEquals(2, list.size());
            Assert.assertEquals(Arrays.asList(0, 2), list);
        }
    }

    @Test
    public void readWriteTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            List<Integer> list = new MemoryBlockLinkedList<>(memory);
            list.add(0);
            list.add(1);
            list.add(2);

            Assert.assertEquals(3, list.size());
            Assert.assertEquals(new Integer(0), list.get(0));
            Assert.assertEquals(new Integer(1), list.get(1));
            Assert.assertEquals(new Integer(2), list.get(2));
        }
    }

    @Test
    public void resizeTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            List<Integer> list = new MemoryBlockLinkedList<>(memory);
            list.add(0);
            list.add(1);
            list.add(2);

            Assert.assertEquals(3, list.size());
            Assert.assertEquals(Arrays.asList(0, 1, 2), list);
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            new MemoryBlockLinkedList<>(memory).add(1000, 0);
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeOutOfBoundsTest() {
        try (MemoryBlock<Integer> memory = new SingleLinkedMemoryBlock<>(Integer.SIZE)) {
            new MemoryBlockLinkedList<>(memory).add(-1, 0);
        }
    }
}
