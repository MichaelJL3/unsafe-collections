package net.ml.unsafe.collections.serialize;

/**
 * Byte serializer factory
 *
 * @author micha
 */
public final class ByteSerializerFactory {
    private ByteSerializerFactory() {}

    /**
     * Get a byte serializer
     *
     * @param type the type of serializer to retrieve
     * @param <T> the object type to serialize
     * @return the byte serializer
     */
    public static <T> ByteSerializer<T> getSerializer(ByteSerializerType type) {
        switch(type) {
            case ARRAY_STREAM_SERIALIZER:
                return new ByteStreamSerializer<>();
            case KRYO_SERIALIZER:
            default:
                return new KryoSerializer<>();
        }
    }

    /**
     * Get the default byte serializer
     *
     * @param <T> the object type to serialize
     * @return the byte serializer
     */
    public static <T> ByteSerializer<T> getDefault() {
        return getSerializer(ByteSerializerType.DEFAULT);
    }
}
