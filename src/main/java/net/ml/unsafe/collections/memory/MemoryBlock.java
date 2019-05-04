package net.ml.unsafe.collections.memory;

public interface MemoryBlock<T> extends Iterable<T> {
    void malloc(int capacity);
    void realloc(int capacity);
    T get(int index);
    void put(int index, T o);
    void free();
    void swap(int indexA, int indexB);
    void copy(int indexA, int indexB);
    int size();
}
