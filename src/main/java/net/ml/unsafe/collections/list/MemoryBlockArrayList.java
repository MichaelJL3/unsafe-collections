package net.ml.unsafe.collections.list;

import net.ml.unsafe.collections.memory.MemoryBlock;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * MemoryBlockArrayList using managed memory allocation
 *
 * @author micha
 * @param <T> the type to store in the arraylist
 */
public class MemoryBlockArrayList<T> extends AbstractList<T> implements List<T> {
    private final MemoryBlock<T> memory;
    private int size = 0;

    public MemoryBlockArrayList(MemoryBlock<T> block) {
        this.memory = block;
    }

    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.get(index);
    }

    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        T old = memory.get(index);
        memory.put(index, element);
        return old;
    }

    @Override
    public void add(int index, T element) {
        if (isFull()) resize();
        if (additionOutOfBounds(index)) throw new IndexOutOfBoundsException();

        for (int i = size(); i > index; --i) {
            memory.copy(i - 1, i);
        }

        memory.put(index, element);
        ++size;
    }

    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        T o = memory.get(index);

        IntStream.range(index, size() - 1).forEach(i -> memory.copy(i + 1, i));

        --size;
        return o;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        memory.free();
        size = 0;
    }

    private boolean isFull() {
        return memory.size() == size();
    }

    private void resize() {
        memory.realloc(grow());
    }

    private int grow() {
        int size = size();
        return size + (size > 1 ? (size >> 1) : 1);
    }

    private boolean outOfBounds(int index) {
        return index < 0 || index >= size();
    }

    private boolean additionOutOfBounds(int index) {
        return index < 0 || index > size();
    }
}
