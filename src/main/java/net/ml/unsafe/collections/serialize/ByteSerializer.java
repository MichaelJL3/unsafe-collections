package net.ml.unsafe.collections.serialize;

/**
 * Byte serializer interface
 *
 * Serialize and deserialize objects into byte arrays
 *
 * @author micha
 * @param <I> the serialized input type
 */
public interface ByteSerializer<I> extends Serializer<I, byte[]> {}
