package net.ml.unsafe.collections.memory;

/**
 * Memory interface
 *
 * Manages chunks of memory
 *
 * @author micha
 */
public interface Memory {
    /**
     * Allocate memory
     *
     * @param size the number of bytes to allocate
     * @return the start address of the allocated memory
     */
    long malloc(int size);

    /**
     * Increase the size of a memory allocation
     *
     * @param address the address of the original memory allocation
     * @param prevSize the original number of bytes allocated
     * @param size the number of bytes to
     * @return the address of the increased allocation
     */
    long realloc(long address, int prevSize, int size);

    /**
     * Release the allocated memory
     *
     * @param address the address to release
     */
    void free(long address);

    /**
     * Place the bytes into memory
     *
     * @param address the address to store the bytes
     * @param bytes the bytes to store
     */
    void put(long address, byte[] bytes);

    /**
     * Swap the bytes of two addresses
     *
     * @param addressA the first address in memory
     * @param addressB the second address in memory
     * @param size the number of bytes to swap
     */
    void swap(long addressA, long addressB, int size);

    /**
     * Copy the bytes of one address into another
     *
     * @param addressA the address to copy from
     * @param addressB the address to copy to
     * @param size the number of bytes to copy
     */
    void copy(long addressA, long addressB, int size);

    /**
     * Get the bytes at the address
     *
     * @param address the start address of the bytes
     * @param size the number of bytes to retrieve
     * @return the bytes retrieved
     */
    byte[] get(long address, int size);
}
