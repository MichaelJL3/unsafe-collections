package net.ml.unsafe.collections.serialize;

import java.io.Serializable;

/**
 * Byte serializer factory
 *
 * @author micha
 */
public final class ByteSerializerFactory {
    /**
     * Get a byte serializer
     *
     * @param type the type of serializer to retrieve
     * @param <O> the object type to serialize
     * @return the byte serializer
     */
    public static <O extends Serializable> ByteSerializer<O> getSerializer(ByteSerializerType type) {
        switch(type) {
            case BUFFER_STREAM_SERIALIZER:
                return new ByteStreamBufferSerializer<>();
            case ARRAY_STREAM_SERIALIZER:
                return new ByteStreamSerializer<>();
            default:
                return new ByteStreamSerializer<>();
        }
    }
}
