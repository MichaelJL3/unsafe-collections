package net.ml.unsafe.collections.memory.blocks;

import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.memory.blocks.models.MemoryLinkedNode;
import net.ml.unsafe.collections.memory.blocks.models.MemoryNode;
import net.ml.unsafe.collections.memory.blocks.models.Reference;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;
import net.ml.unsafe.collections.serialize.ReferenceSerializer;

import java.util.stream.IntStream;

/**
 * Manages a chunk of memory as blocks of references to objects using unsafe
 *
 * Uses memory block of references to addresses in memory of objects allowing
 * for allocating unknown sized or more complex classes
 *
 * @author micha
 * @param <T> the classType of object to store
 */
public final class MemoryLinkedReferenceBlock<T> implements MemoryBlock<T> {
    private final Memory memory;
    //memory block for references
    private final MemoryBlock<MemoryNode<Reference>> refMemory;
    private final ByteSerializer<T> serializer;

    /**
     * Constructor
     * Uses byte serializer factory default serializer
     * Uses memory factory default
     */
    public MemoryLinkedReferenceBlock() {
        this(ByteSerializerFactory.getDefault(), MemoryFactory.getDefault());
    }

    /**
     * Constructor
     *
     * @param serializer byte serializer
     */
    public MemoryLinkedReferenceBlock(ByteSerializer<T> serializer, Memory memory) {
        this.serializer = serializer;
        this.memory = memory;
        //create an inner block with special serializer for references
        this.refMemory = new MemorySingleLinkedBlock<>(Reference.size(), new ReferenceSerializer());
    }

    /**
     * Release allocated memory from objects and references using unsafe
     */
    @Override
    public void free() {
        IntStream.range(0, size()).forEach(i -> {
            Reference ref = refMemory.get(i).getValue();
            if (ref.addr > 0) memory.free(ref.addr);
        });

        refMemory.free();
    }

    /**
     * Swap the references at the two indexes in memory using unsafe
     *
     * @param indexA the index of the first object
     * @param indexB the index of the second object
     */
    @Override
    public void swap(int indexA, int indexB) {
        refMemory.swap(indexA, indexB);
    }

    /**
     * Copy the object from one index in memory to another and create a new reference using unsafe
     *
     * @param indexA the index of the object to copy
     * @param indexB the index to copy the object to
     */
    @Override
    public void copy(int indexA, int indexB) {
        Reference refB = refMemory.get(indexB).getValue();
        if (refB.addr != 0) memory.free(refB.addr);

        put(indexB, get(indexA));
    }

    /**
     * Number of blocks allocated in memory
     *
     * @return the number of blocks
     */
    @Override
    public int size() {
        return refMemory.size();
    }

    /**
     * Allocate memory for n objects using unsafe
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void malloc(int capacity) {
        refMemory.malloc(capacity);
    }

    /**
     * Increase memory allocation while preserving existing allocations data using unsafe
     *
     * @param capacity the number of objects to allocate memory for
     */
    @Override
    public void realloc(int capacity) {
        refMemory.realloc(capacity);
    }

    /**
     * Get the reference and object stored at the index from memory using unsafe
     *
     * @param index the index in memory
     * @return the object retrieved
     */
    @Override
    public T get(int index) {
        Reference ref = refMemory.get(index).getValue();
        return serializer.deserialize(memory.get(ref.addr, ref.length));
    }

    /**
     * Store the object and a reference in memory at the index using unsafe
     *
     * @param index the index in the block to store
     * @param o the object to store
     */
    @Override
    public void put(int index, T o) {
        byte[] bytes = serializer.serialize(o);
        long addr = memory.malloc(bytes.length);
        memory.put(addr, bytes);

        refMemory.put(index, new MemoryLinkedNode<>(0, 0, 0, new Reference(addr, bytes.length)));
    }

    /**
     * Replace the object at the index
     *
     * @param index the index to replace
     * @param o the value to replace with
     * @return the replaced object
     */
    @Override
    public T replace(int index, T o) {
        byte[] bytes = serializer.serialize(o);
        long addr = memory.malloc(bytes.length);
        memory.put(addr, bytes);

        Reference ref = refMemory.get(index).getValue();
        T old = serializer.deserialize(memory.get(ref.addr, ref.length));
        memory.free(ref.addr);

        refMemory.replace(index, new MemoryLinkedNode<>(0, 0, 0, new Reference(addr, bytes.length)));
        return old;
    }

    /**
     * Remove the object at the index
     *
     * @param index the index to remove
     * @return null
     * @throws UnsupportedOperationException cannot remove from array
     */
    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }
}
