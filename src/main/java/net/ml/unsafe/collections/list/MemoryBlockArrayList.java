package net.ml.unsafe.collections.list;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * MemoryBlockArrayList using managed memory allocation
 *
 * @author micha
 * @param <T> the type to store in the arraylist
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryBlockArrayList<T> extends AbstractList<T> implements List<T>, KryoSerializable {
    private MemoryBlock<T> memory;
    private int size = 0;

    /**
     * Constructor
     *
     * @param memory the memory block
     */
    public MemoryBlockArrayList(MemoryBlock<T> memory) {
        this.memory = memory;
    }

    /**
     * Get the object at the index
     *
     * @param index the index to retrieve
     * @return the object retrieved
     */
    @Override
    public T get(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.get(index);
    }

    /**
     * Set the object at the index
     *
     * @param index the index to set
     * @param element the object to set
     * @return the previous value at the index
     */
    @Override
    public T set(int index, T element) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();
        return memory.replace(index, element);
    }

    /**
     * Add an object at the specified index
     *
     * @param index the index to add at
     * @param element the object to add
     */
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

    /**
     * Remove the object at the index
     *
     * @param index the index to remove the object of
     * @return the removed object
     */
    @Override
    public T remove(int index) {
        if (outOfBounds(index)) throw new IndexOutOfBoundsException();

        T o = memory.get(index);

        IntStream.range(index, size() - 1).forEach(i -> memory.copy(i + 1, i));

        --size;
        return o;
    }

    /**
     * Get the number of elements in the list
     *
     * @return the size of the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Remove all the objects from the list
     */
    @Override
    public void clear() {
        memory.free();
        size = 0;
    }

    /**
     * Check if the list is full
     *
     * @return whether or not the list is full
     */
    private boolean isFull() {
        return memory.size() == size();
    }

    /**
     * Resize the list to increase storge capacity
     */
    private void resize() {
        memory.realloc(grow());
    }

    /**
     * Calculate the new size of the list
     *
     * @return the increased list size
     */
    private int grow() {
        int size = size();
        return size + (size > 1 ? (size >> 1) : 1);
    }

    /**
     * Check that the index is within the lists bounds
     *
     * @param index the index to check
     * @return whether or not the index is within bounds
     */
    private boolean outOfBounds(int index) {
        return index < 0 || index >= size();
    }

    /**
     * Check that the index is within the lists bounds
     * while allowing the next accessible index to be considered within bounds
     *
     * @param index the index to check
     * @return whether or not the index is within bounds
     */
    private boolean additionOutOfBounds(int index) {
        return index < 0 || index > size();
    }

    /**
     * Serialize an arraylist
     *
     * @param kryo the kryo reference
     * @param output the output to write the values to
     */
    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(size());
        kryo.writeClassAndObject(output, memory);
    }

    /**
     * Deserialize an arraylist
     *
     * @param kryo the kryo reference
     * @param input the input to get the values from
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(Kryo kryo, Input input) {
        size = input.readInt();
        memory = (MemoryBlock<T>) kryo.readClassAndObject(input);
    }
}
