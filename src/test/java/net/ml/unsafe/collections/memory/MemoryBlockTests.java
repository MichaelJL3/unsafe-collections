package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryReferenceBlock;
import net.ml.unsafe.collections.model.Container;
import org.junit.Assert;
import org.junit.Test;

public class MemoryBlockTests {
    private MemoryBlock<Container<Integer>> memory;

    @Test
    public void allocationTest() {
        int size = 10;
        memory = new MemoryReferenceBlock<>(size);
        Assert.assertEquals(size, memory.size());

        memory.free();
        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void reallocationTest() {
        int origSize = 10;
        memory = new MemoryReferenceBlock<>(origSize);
        Assert.assertEquals(origSize, memory.size());

        int size = 20;
        memory.realloc(size);
        Assert.assertEquals(size, memory.size());

        memory.free();
        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void storageTest() {
        memory = new MemoryReferenceBlock<>();

        Container<Integer> contentOne = new Container<>();
        Container<Integer> contentTwo = new Container<>();
        contentOne.y = 3;
        contentTwo.z = 5;

        memory.put(0, contentOne);
        memory.put(1, contentTwo);

        Assert.assertEquals(contentOne, memory.get(0));
        Assert.assertEquals(contentTwo, memory.get(1));

        memory.free();
    }

    @Test
    public void swapTest() {
        memory = new MemoryReferenceBlock<>();

        Container<Integer> contentOne = new Container<>();
        Container<Integer> contentTwo = new Container<>();
        contentOne.y = 3;
        contentTwo.z = 5;

        memory.put(0, contentOne);
        memory.put(1, contentTwo);
        memory.swap(0, 1);

        Assert.assertEquals(contentOne, memory.get(1));
        Assert.assertEquals(contentTwo, memory.get(0));

        memory.free();
    }

    @Test
    public void copyTest() {
        memory = new MemoryReferenceBlock<>();

        Container<Integer> contentOne = new Container<>();
        Container<Integer> contentTwo = new Container<>();
        contentOne.y = 3;
        contentTwo.z = 5;

        memory.put(0, contentOne);
        memory.copy(0, 1);

        Assert.assertEquals(contentOne, memory.get(0));
        Assert.assertEquals(contentOne, memory.get(1));

        memory.free();
    }
}
