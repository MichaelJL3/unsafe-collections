package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.memory.blocks.nodes.MemoryLinkedNode;
import net.ml.unsafe.collections.memory.blocks.nodes.MemoryNode;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;

import java.nio.ByteBuffer;

public final class MemorySingleLinkNodeBlock<T> implements MemoryBlock<MemoryNode<T>> {
    private static final int WORD_SIZE = Long.BYTES;

    private final ByteSerializer<MemoryNode<T>> nodeSerializer = new NodeSerializer();
    private final ByteSerializer<T> serializer;
    private final Memory memory;
    private final int classSize;

    private int size = 0;
    private final MemoryNode<T> head = new MemoryLinkedNode<>();

    public MemorySingleLinkNodeBlock(int classSize) {
        this(classSize, ByteSerializerFactory.getDefault(), MemoryFactory.getDefault());
    }

    public MemorySingleLinkNodeBlock(int classSize, ByteSerializer<T> serializer) {
        this(classSize, serializer, MemoryFactory.getDefault());
    }

    public MemorySingleLinkNodeBlock(int classSize, ByteSerializer<T> serializer, Memory memory) {
        this.classSize = classSize;
        this.serializer = serializer;
        this.memory = memory;
    }

    @Override
    public void malloc(int capacity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void realloc(int capacity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MemoryNode<T> get(int index) {
        return getUnboundable(index);
    }

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

    @Override
    public MemoryNode<T> replace(int index, MemoryNode<T> o) {
        MemoryNode<T> prev = getUnboundable(index - 1);
        MemoryNode<T> node = next(prev);

        o.setNext(node.getNext());

        memory.put(prev.getNext(), nodeSerializer.serialize(o));
        return node;
    }

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

    @Override
    public void free() {
        MemoryNode<T> node = head;

        for (int i = 0; i < size; ++i) {
            node = next(node);
            if (node.getNext() > 0) memory.free(node.getNext());
        }

        size = 0;
    }

    @Override
    public void swap(int indexA, int indexB) {
        MemoryNode<T> tmp = get(indexB);
        put(indexB, get(indexA));
        put(indexA, tmp);
    }

    @Override
    public void copy(int indexA, int indexB) {
        put(indexB, get(indexA));
    }

    @Override
    public int size() {
        return size;
    }

    private MemoryNode<T> getUnboundable(int index) {
        MemoryNode<T> node = head;

        for (int i = 0; i < index; ++i) {
            node = next(node);
        }

        return node;
    }

    private MemoryNode<T> next(MemoryNode<T> node) {
        byte[] bytes = memory.get(node.getNext(), classSize);
        return nodeSerializer.deserialize(bytes);
    }

    private final class NodeSerializer implements ByteSerializer<MemoryNode<T>> {
        private final ByteBuffer byteBuffer = ByteBuffer.allocate(WORD_SIZE + classSize);

        @Override
        public byte[] serialize(MemoryNode<T> input) {
            byteBuffer.clear();
            byteBuffer.putLong(input.getNext());
            byteBuffer.put(serializer.serialize(input.getValue()));

            return byteBuffer.array();
        }

        @Override
        public MemoryNode<T> deserialize(byte[] output) {
            byteBuffer.clear();
            byteBuffer.put(output, 0, classSize);
            T val = serializer.deserialize(new byte[byteBuffer.remaining()]);

            return new MemoryLinkedNode<>(0, 0, byteBuffer.getLong(0), val);
        }
    }
}
