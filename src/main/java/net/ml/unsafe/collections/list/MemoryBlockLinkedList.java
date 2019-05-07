package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.blocks.nodes.MemoryLinkedNode;
import net.ml.unsafe.collections.memory.blocks.nodes.MemoryNode;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;

import java.util.AbstractList;

public class MemoryBlockLinkedList<T> extends AbstractList<T> {
    private final MemoryBlock<MemoryNode<T>> memory;

    public MemoryBlockLinkedList(MemoryBlock<MemoryNode<T>> memory) {
        this.memory = memory;
    }

    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.get(index).getValue();
    }

    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.replace(index, new MemoryLinkedNode<>(0, 0, 0, element)).getValue();
    }

    @Override
    public void add(int index, T element) {
        if (additionOutOfBounds(index)) throw new IndexOutOfBoundsException();
        memory.put(index, new MemoryLinkedNode<>(0, 0, 0, element));
    }

    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.remove(index).getValue();
    }

    @Override
    public int size() {
        return memory.size();
    }

    @Override
    public void clear() {
        memory.free();
    }

    private boolean outOfBounds(int index) {
        return index >= memory.size() || index < 0;
    }

    private boolean additionOutOfBounds(int index) {
        return index > memory.size() || index < 0;
    }
}
