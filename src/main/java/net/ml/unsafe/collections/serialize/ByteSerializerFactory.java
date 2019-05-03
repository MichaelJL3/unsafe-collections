package net.ml.unsafe.collections.serialize;

import java.io.Serializable;

/**
 * Byte serializer factory
 *
 * @author micha
 */
public class ByteSerializerFactory {
    /**
     * Get byte a byte serializer
     *
     * @param type the type of serializer to retrieve
     * @param <O> the object to serialize
     * @return the byte serializer
     */
    public static <O> ByteSerializer<O> getSerializer(ByteSerializerType type) {
        switch(type) {
            case BUFFER_STREAM_SERIALIZER:
                return new ByteStreamBufferSerializer<>();
            case REFLECTIVE_FIELD_SERIALIZER:
                return null;
            case ARRAY_STREAM_SERIALIZER:
                return new ByteStreamSerializer<>();
            case KRYO_SERIALIZER:
            default:
                return new KryoSerializer<>();
        }
    }

    public static <O> ByteSerializer<O> getDefaultSerializer() {
        return new KryoSerializer<>();
    }
}
