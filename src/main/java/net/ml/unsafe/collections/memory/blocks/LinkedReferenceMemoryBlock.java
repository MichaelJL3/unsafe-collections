package net.ml.unsafe.collections.memory.blocks;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.memory.blocks.models.Reference;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;
import net.ml.unsafe.collections.serialize.ReferenceSerializer;

import java.util.Optional;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinkedReferenceMemoryBlock<T> extends AbstractMemoryBlock<T> implements MemoryBlock<T> {
    private Memory memory;
    private MemoryBlock<Reference> refMemory;
    private ByteSerializer<T> serializer;

    /**
     * Constructor
     *
     * @param serializer byte serializer
     */
    @Builder
    public LinkedReferenceMemoryBlock(ByteSerializer<T> serializer, Memory memory) {
        this.serializer = Optional.ofNullable(serializer).orElse(ByteSerializerFactory.getSerializer());
        this.memory = Optional.ofNullable(memory).orElse(MemoryFactory.getMemory());
        //create an inner block with special serializer for references
        this.refMemory = new LinkedMemoryBlock<>(Reference.size(), new ReferenceSerializer(), this.memory);
    }

    /**
     * Release allocated memory from objects and references using unsafe
     */
    @Override
    public void free() {
        IntStream.range(0, size()).forEach(i -> {
            Reference ref = refMemory.get(i);
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
        replace(indexB, get(indexA));
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
        return getFromRef(refMemory.get(index));
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

        refMemory.put(index, new Reference(addr, bytes.length));
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
        Reference ref = refMemory.get(index);

        T old = getFromRef(ref);
        put(index, o);

        if (ref.addr > 0) memory.free(ref.addr);

        return old;
    }

    /**
     * Remove the object at the index
     *
     * @param index the index to remove
     * @return the removed object
     */
    @Override
    public T remove(int index) {
        T old = get(index);

        Reference ref = refMemory.remove(index);
        memory.free(ref.addr);

        return old;
    }

    /**
     * Get the value from the reference
     *
     * @param ref the reference of the value
     * @return the value stored at the reference
     */
    private T getFromRef(Reference ref) {
        return ref.addr != 0 ?
                serializer.deserialize(memory.get(ref.addr, ref.length)) :
                null;
    }
}
