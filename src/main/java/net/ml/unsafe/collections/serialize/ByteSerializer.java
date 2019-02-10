package net.ml.unsafe.collections.serialize;

import java.io.*;

/**
 * A serializer interface for byte serialization
 *
 * @param <O> the object to serialize
 */
public interface ByteSerializer<O extends Serializable> extends Serializer<byte[], O> { }
