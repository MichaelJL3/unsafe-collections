package net.ml.unsafe.collections.memory.blocks.nodes;

/**
 * Memory node interface
 *
 * @param <T> the type of object stored in the node
 */
public interface MemoryNode<T> {
    /**
     * Get the address of the next node
     *
     * @return the address
     */
    long getNext();

    /**
     * Get the address of the previous node
     *
     * @return the address
     */
    long getPrev();

    /**
     * Get the address of the node
     *
     * @return the address
     */
    long getAddr();

    /**
     * Get the value of the node
     *
     * @return the value
     */
    T getValue();

    /**
     * Set the address of the next node
     *
     * @param address the address
     */
    void setNext(long address);

    /**
     * Set the address of the previous node
     *
     * @param address the address
     */
    void setPrev(long address);

    /**
     * Set the address of the current node
     *
     * @param address the address
     */
    void setAddr(long address);

    /**
     * Set the value of the node
     *
     * @param value the value
     */
    void setValue(T value);
}
