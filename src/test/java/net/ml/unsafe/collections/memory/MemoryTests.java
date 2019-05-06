package net.ml.unsafe.collections.memory;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class MemoryTests {
    private static final Memory memory = new UnsafeMemory();

    @Test
    public void allocationTest() {
        long addr = memory.malloc(Integer.BYTES);
        log.info("{}", addr);

        Assert.assertTrue(addr > 0);

        memory.free(addr);
    }

    @Test
    public void reallocationTest() {
        long addr = memory.malloc(Integer.BYTES);
        long newAddr = memory.realloc(addr, Integer.BYTES, Integer.BYTES * 3);
        log.info("{} -> {}", addr, newAddr);

        Assert.assertTrue(addr != newAddr);

        memory.free(newAddr);
    }

    @Test
    public void storageTest() {
        long addr = memory.malloc(Integer.BYTES * 2);
        byte[] aBytes = { 0x0, 0x4, 0x0, 0x0 };
        byte[] bBytes = { 0x0, 0x0, 0x1, 0x0 };

        memory.put(addr, aBytes);
        log.info("Storing: {}", aBytes);

        memory.put(addr + Integer.BYTES, bBytes);
        log.info("Storing: {}", bBytes);

        Assert.assertArrayEquals(aBytes, memory.get(addr, Integer.BYTES));
        Assert.assertArrayEquals(bBytes, memory.get(addr + Integer.BYTES, Integer.BYTES));

        memory.free(addr);
    }
}
