package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.Reference;
import net.ml.unsafe.collections.memory.UnsafeMemory;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;

import java.util.AbstractList;

/**
 * MemoryBlockArrayList using unsafe memory allocation
 *
 * @author micha
 * @param <T> the type to store in the linkedlist
 */
public class UnsafeLinkedList<T> extends AbstractList<T> {
    private final Memory memory = new UnsafeMemory();
    private final ByteSerializer<Node> serializer = ByteSerializerFactory.getDefaultSerializer();

    private final Node head = new Node();

    private int size = 0;

    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        Node node = head;
        for (int i = 0; i <= index; ++i) {
            node = serializer.deserialize(get(node.next));
        }

        return node.value;
    }

    private byte[] get(Reference ref) {
        return memory.get(ref.addr, ref.length);
    }

    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        Node node = head;
        Node prev = head;
        for (int i = 0; i <= index; ++i) {
            prev = node;
            node = serializer.deserialize(get(node.next));
        }

        T oldValue = node.value;
        node.value = element;
        memory.put(prev.next.addr, serializer.serialize(node));
        return oldValue;
    }

    @Override
    public void add(int index, T element) {
        if (index > size || index < 0) throw new IndexOutOfBoundsException();

        Node node = head;
        Node prev = head;
        Node newNode = new Node(element);
        for (int i = 0; i <= index; ++i) {
            prev = node;
            node = serializer.deserialize(get(node.next));
        }

        newNode.next = node.next;
        byte[] newNodeBytes = serializer.serialize(node);
        long addr = memory.malloc(newNodeBytes.length);

        node.next = new Reference(addr, newNodeBytes.length);
        memory.put(prev.next.addr, newNodeBytes);
        memory.put(addr, serializer.serialize(newNode));
        ++size;
    }

    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        Node node = head;
        Node prev = head;
        Node prevprev = head;
        for (int i = 0; i <= index; ++i) {
            prevprev = prev;
            prev = node;
            node = serializer.deserialize(get(node.next));
        }

        long addr = prev.next.addr;
        prev.next = node.next;
        T oldVal = node.value;

        memory.free(addr);
        memory.put(prevprev.next.addr, serializer.serialize(prev));

        --size;

        return oldVal;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Node node = head;
        long addr;

        for (int i = 0; i < size; ++i) {
            addr = node.next.addr;
            node = serializer.deserialize(get(node.next));
            memory.free(addr);
        }

        size = 0;
    }

    private boolean outOfBounds(int index) {
        return index >= size || index < 0;
    }

    private class Node {
        Node() {}

        Node(T value) {
            this(value, null);
        }

        Node(T value, Reference next) {
            this.value = value;
            this.next = next;
        }

        T value;
        Reference next;
    }
}
