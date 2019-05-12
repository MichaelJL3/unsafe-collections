package net.ml.unsafe.collections.memory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages chunks of memory while tracking allocations
 *
 * @author micha
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrackedMemory implements Memory {
    private static final Map<Long, Integer> allocations = new ConcurrentHashMap<>();
    private Memory memory;

    /**
     * Constructor
     *
     * @param memory the memory accessor to decorate with traacking
     */
    public TrackedMemory(Memory memory) {
        this.memory = memory;
    }

    /**
     * Get the allocated memory map
     *
     * @return the map of current memory allocations
     */
    public Map<Long, Integer> memoryMap() {
        return allocations;
    }

    /**
     * Find if there are leaks in the memory
     *
     * @return whether or not there are memory leaks
     */
    public boolean hasMemoryLeaks() {
        return allocations.size() > 0;
    }

    /**
     * Allocate memory while tracking the reference and size
     *
     * @param size the number of bytes to allocate
     * @return the start address of the allocated memory
     */
    @Override
    public long malloc(int size) {
        long addr = memory.malloc(size);
        allocations.put(addr, size);
        return addr;
    }

    /**
     * Increase the size of a memory allocation while tracking the reference and size
     *
     * @param address the address of the original memory allocation
     * @param prevSize the previous number of bytes
     * @param size the number of bytes to
     * @return the address of the increased allocation
     */
    @Override
    public long realloc(long address, int prevSize, int size) {
        long addr = memory.realloc(address, prevSize, size);
        allocations.remove(address);
        allocations.put(addr, size);
        return addr;
    }

    /**
     * Release the allocated memory while tracking the reference
     *
     * @param address the address to release
     */
    @Override
    public void free(long address) {
        allocations.remove(address);
        memory.free(address);
    }

    /**
     * Place the bytes into memory
     *
     * @param address the address to store the bytes
     * @param bytes the bytes to store
     */
    @Override
    public void put(long address, byte[] bytes) {
        memory.put(address, bytes);
    }

    /**
     * Swap the bytes of two addresses while logging the addresses and sizes
     *
     * @param addressA the first address in memory
     * @param addressB the second address in memory
     * @param size the number of bytes to swap
     */
    @Override
    public void swap(long addressA, long addressB, int size) {
        memory.swap(addressA, addressB, size);
    }

    /**
     * Copy the bytes of one address into another
     *
     * @param addressA the address to copy from
     * @param addressB the address to copy to
     * @param size the number of bytes to copy
     */
    @Override
    public void copy(long addressA, long addressB, int size) {
        memory.copy(addressA, addressB, size);
    }

    /**
     * Get the bytes at the address
     *
     * @param address the start address of the bytes
     * @param size the number of bytes to retrieve
     * @return the bytes retrieved
     */
    @Override
    public byte[] get(long address, int size) {
        return memory.get(address, size);
    }
}
