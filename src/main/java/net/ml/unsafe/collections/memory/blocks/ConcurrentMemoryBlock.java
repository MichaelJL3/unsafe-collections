package net.ml.unsafe.collections.memory.blocks;

/**
 * Thread safe memory interface
 *
 * Manages chunks of memory with thread safe access
 *
 * @author micha
 * @param <T> the object type to manage in memory
 */
public interface ConcurrentMemoryBlock<T> extends MemoryBlock<T> {}
