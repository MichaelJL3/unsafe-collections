package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.memory.blocks.models.SingleLinkedMemoryNode;
import net.ml.unsafe.collections.memory.blocks.models.MemoryNode;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Manages chunks of memory linked as nodes
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public final class SingleLinkedMemoryBlock<T> implements MemoryBlock<T> {
    private static final int WORD_SIZE = Long.BYTES;
    private static final int ADDRESSES = WORD_SIZE * 2;

    private final ByteSerializer<MemoryNode<T>> nodeSerializer;
    private final ByteSerializer<T> serializer;
    private final Memory memory;
    private final int classSize;

    private int size = 0;
    private final MemoryNode<T> head = new SingleLinkedMemoryNode<>();
    private MemoryNode<T> tail = head;

    /**
     * Constructor
     * Uses byte serializer factory default serializer
     * Uses memory factory default
     *
     * @param classSize number of bytes per object
     */
    public SingleLinkedMemoryBlock(int classSize) {
        this(classSize, ByteSerializerFactory.getDefault(), MemoryFactory.getDefault());
    }

    /**
     * Constructor
     * Uses memory factory default
     *
     * @param classSize number of bytes per object
     * @param serializer byte serializer
     */
    public SingleLinkedMemoryBlock(int classSize, ByteSerializer<T> serializer) {
        this(classSize, serializer, MemoryFactory.getDefault());
    }

    /**
     * Constructor
     *
     * @param classSize number of bytes per object
     * @param serializer byte serializer
     * @param memory the memory wrapper
     */
    public SingleLinkedMemoryBlock(int classSize, ByteSerializer<T> serializer, Memory memory) {
        this.classSize = classSize;
        this.serializer = serializer;
        this.memory = memory;
        this.nodeSerializer = new NodeSerializer();
    }

    /**
     * Allocate memory for n objects
     *
     * @param capacity the number of objects to allocate memory for
     * @throws UnsupportedOperationException nodes are allocated on an insertion basis
     */
    @Override
    public void malloc(int capacity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Increase memory allocation while preserving existing allocations data
     *
     * @param capacity the number of objects to allocate memory for
     * @throws UnsupportedOperationException nodes are allocated on an insertion basis
     */
    @Override
    public void realloc(int capacity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the node stored at the index from memory
     *
     * @param index the index in memory
     * @return the node retrieved
     */
    @Override
    public T get(int index) {
        return getUnboundable(index).getValue();
    }

    /**
     * Store the node in memory at the index
     *
     * @param index the index in the node list to store
     * @param o the object to store
     */
    @Override
    public void put(int index, T o) {
        if (index == size) {
            insertAtEnd(o);
            return;
        }

        long addr = memory.malloc(ADDRESSES + classSize);

        MemoryNode<T> prev = getUnboundable(index - 1);
        MemoryNode<T> node = new SingleLinkedMemoryNode<>(addr, prev.getNext(), o);

        memory.put(addr, nodeSerializer.serialize(node));

        prev.setNext(addr);
        memory.put(prev.getAddr(), nodeSerializer.serialize(prev));

        ++size;
    }

    /**
     * Replace the node at the index
     *
     * @param index the index to replace
     * @param o the node to replace with
     * @return the replaced node
     */
    @Override
    public T replace(int index, T o) {
        MemoryNode<T> node = getUnboundable(index);
        MemoryNode<T> newNode = new SingleLinkedMemoryNode<>(node.getAddr(), node.getNext(), o);

        memory.put(node.getAddr(), nodeSerializer.serialize(newNode));
        return node.getValue();
    }

    /**
     * Remove the node at the index
     *
     * @param index the index to remove
     * @return the removed node
     */
    @Override
    public T remove(int index) {
        MemoryNode<T> prev = getUnboundable(index - 1);
        MemoryNode<T> node = next(prev);

        memory.free(node.getAddr());

        prev.setNext(node.getNext());
        if (index != 0) {
            memory.put(prev.getAddr(), nodeSerializer.serialize(prev));
        }

        --size;
        return node.getValue();
    }

    /**
     * Release allocated nodes
     */
    @Override
    public void free() {
        MemoryNode<T> node = head;

        for (int i = 0; i < size; ++i) {
            node = next(node);
            if (node.getNext() > 0) memory.free(node.getNext());
        }

        size = 0;
    }

    /**
     * Swap the nodes at the two indexes in memory
     *
     * @param indexA the index of the first node
     * @param indexB the index of the second node
     */
    @Override
    public void swap(int indexA, int indexB) {
        T tmp = get(indexB);
        replace(indexB, get(indexA));
        replace(indexA, tmp);
    }

    /**
     * Copy the node from one index in memory to another
     *
     * @param indexA the index of the node to copy
     * @param indexB the index to copy the node to
     */
    @Override
    public void copy(int indexA, int indexB) {
        replace(indexB, get(indexA));
    }

    /**
     * Number of blocks allocated in memory
     *
     * @return the number of blocks
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Insert an object at the tail of the list
     *
     * @param val the object to insert
     */
    private void insertAtEnd(T val) {
        MemoryNode<T> node = new SingleLinkedMemoryNode<>(0, 0, val);

        long addr = memory.malloc(ADDRESSES + classSize);

        node.setAddr(addr);
        memory.put(addr, nodeSerializer.serialize(node));

        tail.setNext(addr);
        if (!isEmpty()) {
            memory.put(tail.getAddr(), nodeSerializer.serialize(tail));
        }

        ++size;
        tail = node;
    }

    /**
     * Check if the list is empty
     *
     * @return whether or not the list is empty
     */
    private boolean isEmpty() {
        return size == 0;
    }

    /**
     * Get the node at the index without a minimum bound check
     * this allow negatives to default to the head
     *
     * @param index the index of the node to get
     * @return the node
     */
    private MemoryNode<T> getUnboundable(int index) {
        MemoryNode<T> node = head;

        for (int i = 0; i <= index; ++i) {
            node = next(node);
        }

        return node;
    }

    /**
     * Get the node after the specified node
     *
     * @param node the node to fetch the next of
     * @return the next node
     */
    private MemoryNode<T> next(MemoryNode<T> node) {
        byte[] bytes = memory.get(node.getNext(), ADDRESSES + classSize);
        return nodeSerializer.deserialize(bytes);
    }

    /**
     * Serializer for singly linked memory nodes
     *
     * @author micha
     */
    private final class NodeSerializer implements ByteSerializer<MemoryNode<T>> {
        private final ByteBuffer byteBuffer = ByteBuffer.allocate(ADDRESSES + classSize);

        /**
         * Serialize the node into a byte array
         *
         * @param input the node to serialize
         * @return the node as bytes
         */
        @Override
        public byte[] serialize(MemoryNode<T> input) {
            byteBuffer.clear();
            byteBuffer.putLong(input.getAddr());
            byteBuffer.putLong(input.getNext());
            byteBuffer.put(serializer.serialize(input.getValue()));

            return byteBuffer.array();
        }

        /**
         * Deserialize a byte array into a node object
         *
         * @param output the bytes to deserialize
         * @return the deserialized node
         */
        @Override
        public MemoryNode<T> deserialize(byte[] output) {
            byte[] addresses = Arrays.copyOfRange(output, 0, ADDRESSES);
            byte[] bytes = Arrays.copyOfRange(output, ADDRESSES, output.length);
            byteBuffer.clear();
            byteBuffer.put(addresses);
            T val = serializer.deserialize(bytes);

            return new SingleLinkedMemoryNode<>(byteBuffer.getLong(0), byteBuffer.getLong(WORD_SIZE), val);
        }
    }
}
