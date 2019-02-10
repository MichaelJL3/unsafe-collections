package net.ml.unsafe.collections.memory;

import java.io.Serializable;

/**
 * Access memory Concurrently
 *
 * @param <T> the type of object to manage the memory of
 */
public interface ConcurrentMemory<T extends Serializable> extends Memory<T> { }
