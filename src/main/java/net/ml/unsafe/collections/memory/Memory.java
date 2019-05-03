package net.ml.unsafe.collections.memory;

/**
 * Managed memory interface
 *
 * Manages chunks of memory of the associated model
 *
 * @author micha
 */
public interface Memory {
    //allocate memory
    long malloc(int size);
    //reallocate memory
    long realloc(long address, int size);
    //free the memory chunk
    void free(long address);
    //place the object bytes into the specified segment
    void put(long address, byte[] bytes);
    //swap two objects by the block indexes
    void swap(long addressA, long addressB, int size);
    //copy one object block into another
    void copy(long addressA, long addressB, int size);
    //get the object at the block id
    byte[] get(long address, int size);
}
