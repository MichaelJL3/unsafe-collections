package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.UnsafeMemory;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;

import java.util.AbstractList;

/**
 * ArrayList using unsafe memory allocation
 *
 * @author micha
 * @param <T> the type to store in the linkedlist
 */
public class UnsafeLinkedList<T> extends AbstractList<T> {
    private final Memory memory = new UnsafeMemory();
    private final ByteSerializer<UnsafeNode> serializer = ByteSerializerFactory.getDefaultSerializer();

    private final UnsafeNode head = new UnsafeNode();

    private int size = 0;
    private final int classSize;

    public UnsafeLinkedList(Class<T> type) {
        classSize = 0;
    }

    /**
     * Get the object at the specified index
     *
     * @param index the index to retrieve
     * @return the object at the index
     *
     * @throws IndexOutOfBoundsException accessing index outside of the linked list
    */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        UnsafeNode node = head;
        for (int i = 0; i <= index; ++i) {
            node = serializer.deserialize(memory.get(node.next, classSize));
        }

        return node.value;
    }

    /**
     * Set the object in the specified index of the list
     *
     * @param index the index to set the value of
     * @param element the element to set
     * @return the element replaced
     *
     * @throws IndexOutOfBoundsException accessing index outside of the linked list
     */
    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        UnsafeNode node = head;
        UnsafeNode prev = head;
        for (int i = 0; i <= index; ++i) {
            prev = node;
            node = serializer.deserialize(memory.get(node.next, classSize));
        }

        T oldValue = node.value;
        node.value = element;
        memory.put(prev.next, serializer.serialize(node));
        return oldValue;
    }

    /**
     * Add a new element to the list
     *
     * Causes a shift of the element if not at the end
     *
     * @param index the index to insert at
     * @param element the element to insert
     *
     * @throws IndexOutOfBoundsException attempting to add past the end of the list
     */
    @Override
    public void add(int index, T element) {
        if (index > size || index < 0) throw new IndexOutOfBoundsException();

        UnsafeNode node = head;
        UnsafeNode prev = head;
        UnsafeNode newNode = new UnsafeNode(element);
        for (int i = 0; i <= index; ++i) {
            prev = node;
            node = serializer.deserialize(memory.get(node.next, classSize));
        }

        long addr = memory.malloc(classSize);
        newNode.next = node.next;
        node.next = addr;
        memory.put(prev.next, serializer.serialize(node));
        memory.put(addr, serializer.serialize(newNode));
        ++size;
    }

    /**
     * Remove the element at the specified index
     *
     * Causes the other elements to shift if not at the end
     *
     * @param index the index to remove
     * @return the removed element
     *
     * @throws IndexOutOfBoundsException accessing index outside of the linked list
     */
    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        UnsafeNode node = head;
        UnsafeNode prev = head;
        UnsafeNode prevprev = head;
        for (int i = 0; i <= index; ++i) {
            prevprev = prev;
            prev = node;
            node = serializer.deserialize(memory.get(node.next, classSize));
        }

        long addr = prev.next;
        prev.next = node.next;
        T oldVal = node.value;

        memory.free(addr);
        memory.put(prevprev.next, serializer.serialize(prev));

        --size;

        return oldVal;
    }

    /**
     * Get the number of items in the linkedlist
     *
     * @return the number of items in the linkedlist
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Clear the contents of the list
     */
    @Override
    public void clear() {
        UnsafeNode node = head;
        long addr;

        for (int i = 0; i < size; ++i) {
            addr = node.next;
            node = serializer.deserialize(memory.get(addr, classSize));
            memory.free(addr);
        }

        size = 0;
    }

    private boolean outOfBounds(int index) {
        return index >= size || index < 0;
    }

    private class UnsafeNode {
        UnsafeNode() {}

        UnsafeNode(T value) {
            this(value, 0);
        }

        UnsafeNode(T value, long next) {
            this.value = value;
            this.next = next;
        }

        T value;
        long next;
    }
}
