package net.ml.unsafe.collections.memory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages chunks of memory while logging accesses
 *
 * @author micha
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggedMemory implements Memory{
    private Memory memory;

    /**
     * Constructor
     *
     * @param memory the memory accessor to decorate with logging
     */
    public LoggedMemory(Memory memory) {
        this.memory = memory;
    }

    /**
     * Allocate memory while logging the addresses and sizes
     *
     * @param size the number of bytes to allocate
     * @return the start address of the allocated memory
     */
    @Override
    public long malloc(int size) {
        log.debug("Allocating [{}]", size);
        long addr = memory.malloc(size);
        log.debug("Allocated @{}[{}]", addr, size);

        return addr;
    }

    /**
     * Increase the size of a memory allocation while logging the addresses and sizes
     *
     * @param address the address of the original memory allocation
     * @param prevSize the previous number of bytes
     * @param size the number of bytes to
     * @return the address of the increased allocation
     */
    @Override
    public long realloc(long address, int prevSize, int size) {
        log.debug("Reallocating @{}[{}] to [{}]", address, prevSize, size);
        long addr = memory.realloc(address, prevSize, size);
        log.debug("Reallocated @{}[{}] to @{}[{}]", address, prevSize, addr, size);

        return addr;
    }

    /**
     * Release the allocated memory while logging the addresses
     *
     * @param address the address to release
     */
    @Override
    public void free(long address) {
        memory.free(address);
        log.debug("Freed @{}", address);
    }

    /**
     * Place the bytes into memory while logging the addresses and sizes
     *
     * @param address the address to store the bytes
     * @param bytes the bytes to store
     */
    @Override
    public void put(long address, byte[] bytes) {
        memory.put(address, bytes);
        log.debug("Stored @{}[{}]", address, bytes.length);
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
        log.debug("Swapping @{}[{}] with @{}[{}]", addressA, size, addressB, size);
        memory.swap(addressA, addressB, size);
    }

    /**
     * Copy the bytes of one address into another while logging the addresses and sizes
     *
     * @param addressA the address to copy from
     * @param addressB the address to copy to
     * @param size the number of bytes to copy
     */
    @Override
    public void copy(long addressA, long addressB, int size) {
        log.debug("Copying @{}[{}] to @{}[{}]", addressA, size, addressB, size);
        memory.copy(addressA, addressB, size);
    }

    /**
     * Get the bytes at the address while logging the address and bytes
     *
     * @param address the start address of the bytes
     * @param size the number of bytes to retrieve
     * @return the bytes retrieved
     */
    @Override
    public byte[] get(long address, int size) {
        log.debug("Retrieving @{}[{}]", address, size);
        return memory.get(address, size);
    }
}
