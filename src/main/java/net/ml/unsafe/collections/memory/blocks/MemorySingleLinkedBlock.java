package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.memory.blocks.models.MemoryLinkedNode;
import net.ml.unsafe.collections.memory.blocks.models.MemoryNode;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;

import java.nio.ByteBuffer;

/**
 * Manages chunks of memory linked as nodes
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public final class MemorySingleLinkedBlock<T> implements MemoryBlock<MemoryNode<T>> {
    private static final int WORD_SIZE = Long.BYTES;

    private final ByteSerializer<MemoryNode<T>> nodeSerializer = new NodeSerializer();
    private final ByteSerializer<T> serializer;
    private final Memory memory;
    private final int classSize;

    private int size = 0;
    private final MemoryNode<T> head = new MemoryLinkedNode<>();

    /**
     * Constructor
     * Uses byte serializer factory default serializer
     * Uses memory factory default
     *
     * @param classSize number of bytes per object
     */
    public MemorySingleLinkedBlock(int classSize) {
        this(classSize, ByteSerializerFactory.getDefault(), MemoryFactory.getDefault());
    }

    /**
     * Constructor
     * Uses memory factory default
     *
     * @param classSize number of bytes per object
     * @param serializer byte serializer
     */
    public MemorySingleLinkedBlock(int classSize, ByteSerializer<T> serializer) {
        this(classSize, serializer, MemoryFactory.getDefault());
    }

    /**
     * Constructor
     *
     * @param classSize number of bytes per object
     * @param serializer byte serializer
     * @param memory the memory wrapper
     */
    public MemorySingleLinkedBlock(int classSize, ByteSerializer<T> serializer, Memory memory) {
        this.classSize = classSize;
        this.serializer = serializer;
        this.memory = memory;
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
    public MemoryNode<T> get(int index) {
        return getUnboundable(index);
    }

    /**
     * Store the node in memory at the index
     *
     * @param index the index in the node list to store
     * @param node the node to store
     */
    @Override
    public void put(int index, MemoryNode<T> node) {
        MemoryNode<T> prev = getUnboundable(index - 1);

        long addr = memory.malloc(WORD_SIZE + classSize);
        long prevAddr = prev.getNext();

        node.setNext(prevAddr);
        prev.setNext(addr);

        memory.put(addr, nodeSerializer.serialize(node));
        memory.put(prevAddr, nodeSerializer.serialize(prev));

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
    public MemoryNode<T> replace(int index, MemoryNode<T> o) {
        MemoryNode<T> prev = getUnboundable(index - 1);
        MemoryNode<T> node = next(prev);

        o.setNext(node.getNext());

        memory.put(prev.getNext(), nodeSerializer.serialize(o));
        return node;
    }

    /**
     * Remove the node at the index
     *
     * @param index the index to remove
     * @return the removed node
     */
    @Override
    public MemoryNode<T> remove(int index) {
        MemoryNode<T> prevRef = getUnboundable(index - 2);
        MemoryNode<T> prev = (index <= 0) ? head : next(prevRef);
        MemoryNode<T> node = next(prev);

        memory.free(prev.getNext());

        prev.setNext(node.getNext());
        memory.put(prevRef.getNext(), nodeSerializer.serialize(prev));

        --size;
        return node;
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
        MemoryNode<T> tmp = get(indexB);
        put(indexB, get(indexA));
        put(indexA, tmp);
    }

    /**
     * Copy the node from one index in memory to another
     *
     * @param indexA the index of the node to copy
     * @param indexB the index to copy the node to
     */
    @Override
    public void copy(int indexA, int indexB) {
        put(indexB, get(indexA));
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
     * Get the node at the index without a minimum bound check
     * this allow negatives to default to the head
     *
     * @param index the index of the node to get
     * @return the node
     */
    private MemoryNode<T> getUnboundable(int index) {
        MemoryNode<T> node = head;

        for (int i = 0; i < index; ++i) {
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
        byte[] bytes = memory.get(node.getNext(), classSize);
        return nodeSerializer.deserialize(bytes);
    }

    /**
     * Serializer for singly linked memory nodes
     *
     * @author micha
     */
    private final class NodeSerializer implements ByteSerializer<MemoryNode<T>> {
        private final ByteBuffer byteBuffer = ByteBuffer.allocate(WORD_SIZE + classSize);

        /**
         * Serialize the node into a byte array
         *
         * @param input the node to serialize
         * @return the node as bytes
         */
        @Override
        public byte[] serialize(MemoryNode<T> input) {
            byteBuffer.clear();
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
            byteBuffer.clear();
            byteBuffer.put(output, 0, classSize);
            T val = serializer.deserialize(new byte[byteBuffer.remaining()]);

            return new MemoryLinkedNode<>(0, 0, byteBuffer.getLong(0), val);
        }
    }
}
