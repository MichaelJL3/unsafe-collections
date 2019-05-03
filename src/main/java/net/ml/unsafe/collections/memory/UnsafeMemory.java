package net.ml.unsafe.collections.memory;

import net.ml.unsafe.collections.util.UnsafeSingleton;
import sun.misc.Unsafe;

/**
 * Manage memory of objects through unsafe
 *
 * @author micha
 */
public final class UnsafeMemory implements Memory {
    private static final Unsafe unsafe = UnsafeSingleton.getUnsafe();

    /**
     * Allocate memory for n objects where n = capacity * sizeOf(T)
     *
     * @param size the number of bytes to allocate
     * @return the new address of the memory allocation
     */
    @Override
    public long malloc(int size) {
        return unsafe.allocateMemory(size);
    }

    /**
     * Reallocate memory to new location with increased size
     *
     * @param address the original address of the memory
     * @param size the amount of bytes to reallocate for
     * @return the new address of the memory reallocation
     */
    @Override
    public long realloc(long address, int size) {
        return unsafe.reallocateMemory(address, size);
    }

    /**
     * Free the allocated memory
     *
     * @param address the address to free
     */
    @Override
    public void free(long address) {
        unsafe.freeMemory(address);
    }

    /**
     * Get the object at the given index
     *
     * @param address the address to retrieve the information from
     * @return the retrieved object bytes
     */
    @Override
    public byte[] get(long address, int size) {
        byte[] bytes = new byte[size];
        loadBytes(address, bytes);
        return bytes;
    }

    /**
     * Put an object into the memory at the given address
     *
     * @param address the address to store the object
     * @param bytes the bytes to store
     */
    @Override
    public void put(long address, byte[] bytes) {
        storeBytes(address, bytes);
    }

    /**
     * Swap the memory blocks
     *
     * @param addressA the first address
     * @param addressB the second address
     * @param size the number of bytes to swap
     */
    @Override
    public void swap(long addressA, long addressB, int size) {
        byte[] tmp = get(addressB, size);
        put(addressB, get(addressA, size));
        put(addressA, tmp);
    }

    /**
     * Copy a memory block to another block
     *
     * @param addressA the source address
     * @param addressB the destination address
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
}
