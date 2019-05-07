package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryLinkedReferenceBlock;
import net.ml.unsafe.collections.memory.blocks.MemorySingleLinkedBlock;
import net.ml.unsafe.collections.memory.blocks.models.MemoryLinkedNode;
import net.ml.unsafe.collections.memory.blocks.models.MemoryNode;
import org.junit.Assert;
import org.junit.Test;

public class MemoryNodeBlockTests {
    private MemoryBlock<MemoryNode<Integer>> memory;

    @Test(expected = UnsupportedOperationException.class)
    public void allocationTest() {
        memory = new MemorySingleLinkedBlock<>(Integer.BYTES);
        memory.malloc(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void reallocationTest() {
        memory = new MemorySingleLinkedBlock<>(Integer.BYTES);
        memory.realloc(1);
    }

    @Test
    public void storageTest() {
        memory = new MemorySingleLinkedBlock<>(Integer.BYTES);

        memory.put(0, new MemoryLinkedNode<>(0, 0, 0, 2));
        memory.put(1, new MemoryLinkedNode<>(0, 0, 0, 3));

        Assert.assertEquals(new Integer(2), memory.get(0).getValue());
        Assert.assertEquals(new Integer(3), memory.get(1).getValue());

        memory.free();
    }

    @Test
    public void swapTest() {
        memory = new MemoryLinkedReferenceBlock<>();

        memory.put(0, new MemoryLinkedNode<>(0, 0, 0, 2));
        memory.put(1, new MemoryLinkedNode<>(0, 0, 0, 3));
        memory.swap(0, 1);

        Assert.assertEquals(new Integer(3), memory.get(0).getValue());
        Assert.assertEquals(new Integer(2), memory.get(1).getValue());

        memory.free();
    }

    @Test
    public void copyTest() {
        memory = new MemoryLinkedReferenceBlock<>();

        memory.put(0, new MemoryLinkedNode<>(0, 0, 0, 2));
        memory.copy(0, 1);

        Assert.assertEquals(new Integer(2), memory.get(0).getValue());
        Assert.assertEquals(new Integer(2), memory.get(1).getValue());

        memory.free();
    }
}
