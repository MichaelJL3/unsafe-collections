package net.ml.unsafe.collections;

/**
 * Managed memory interface
 *
 * Manages chunks of memory of the associated model
 *
 * @author micha
 * @param <T> the type of object to manage the memory of
 */
public interface Memory<T> {
    //allocate memory
    void malloc(int size);
    //reallocate memory
    void realloc(int size);
    //free the memory chunk
    void free();
    //place the object into the specified segment
    void put(int index, T o);
    //swap two objects by the block indexes
    void swap(int indexA, int indexB);
    //copy one object block into another
    void copy(int indexA, int indexB);
    //get the object at the block id
    T get(int index);
    //get the object at the block id
    void get(int index, T o);
    //get the number of segment ids in the memory chunk
    int size();
}
