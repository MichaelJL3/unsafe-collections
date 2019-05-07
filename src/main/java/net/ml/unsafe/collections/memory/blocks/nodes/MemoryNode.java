package net.ml.unsafe.collections.memory.blocks.nodes;

public interface MemoryNode<T> {
    long getNext();
    long getPrev();
    long getAddr();
    T getValue();

    void setNext(long value);
    void setPrev(long value);
    void setAddr(long value);
    void setValue(T value);
}
