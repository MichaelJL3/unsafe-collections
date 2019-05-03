package net.ml.unsafe.collections.memory;

public interface MemoryBlock<T> {
    void malloc(int capacity);
    void realloc(int capacity);
    T get(int index);
    void put(int index, T o);
    void free();
    int size();
}
