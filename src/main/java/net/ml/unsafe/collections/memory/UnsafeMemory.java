package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.util.UnsafeSingleton;
import sun.misc.Unsafe;

/**
 * Manages chunks of memory using unsafe
 *
 * @author micha
 */
public final class UnsafeMemory implements Memory {
    private static final Unsafe unsafe = UnsafeSingleton.getUnsafe();

    /**
     * Allocate memory using unsafe
     *
     * @param size the number of bytes to allocate
     * @return the start address of the allocated memory
     */
    @Override
    public long malloc(int size) {
        long addr = unsafe.allocateMemory(size);
        zeroData(addr, size);
        return addr;
    }

    /**
     * Increase the size of a memory allocation using unsafe
     *
     * @param address the address of the original memory allocation
     * @param size the number of bytes to
     * @return the address of the increased allocation
     */
    @Override
    public long realloc(long address, int size) {
        long addr = unsafe.reallocateMemory(address, size);
        zeroData(addr, size);
        return addr;
    }

    /**
     * Release the allocated memory using unsafe
     *
     * @param address the address to release
     */
    @Override
    public void free(long address) {
        unsafe.freeMemory(address);
    }

    /**
     * Get the bytes at the address using unsafe
     *
     * @param address the start address of the bytes
     * @param size the number of bytes to retrieve
     * @return the bytes retrieved
     */
    @Override
    public byte[] get(long address, int size) {
        byte[] bytes = new byte[size];
        loadBytes(address, bytes);
        return bytes;
    }

    /**
     * Place the bytes into memory using unsafe
     *
     * @param address the address to store the bytes
     * @param bytes the bytes to store
     */
    @Override
    public void put(long address, byte[] bytes) {
        storeBytes(address, bytes);
    }

    /**
     * Swap the bytes of two addresses using unsafe
     *
     * @param addressA the first address in memory
     * @param addressB the second address in memory
     * @param size the number of bytes to swap
     */
    @Override
    public void swap(long addressA, long addressB, int size) {
        byte[] tmp = get(addressB, size);
        put(addressB, get(addressA, size));
        put(addressA, tmp);
    }

    /**
     * Copy the bytes of one address into another using unsafe
     *
     * @param addressA the address to copy from
     * @param addressB the address to copy to
     * @param size the number of bytes to copy
     */
    @Override
    public void copy(long addressA, long addressB, int size) {
        put(addressB, get(addressA, size));
    }

    /**
     * Load bytes from unsafe memory
     *
     * @param address the memory address
     * @param bytes the buffer to hold the loaded bytes
     */
    private void loadBytes(long address, byte[] bytes) {
        unsafe.copyMemory(null, address, bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, bytes.length);
    }

    /**
     * Store bytes in unsafe memory
     *
     * @param address the memory address
     * @param bytes the bytes to store
     */
    private void storeBytes(long address, byte[] bytes) {
        unsafe.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, bytes.length);
    }

    /**
     * Zero out the data at the address
     *
     * @param address the address to clear
     * @param size the number of bytes to clear
     */
    private void zeroData(long address, int size) {
        storeBytes(address, new byte[size]);
    }
}
