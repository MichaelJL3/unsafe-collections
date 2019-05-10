package net.ml.unsafe.collections.serialize;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Byte serializer factory
 *
 * @author micha
 */
public final class ByteSerializerFactory {
    private ByteSerializerFactory() {}

    private static final Map<String, Supplier<ByteSerializer<?>>> registered = new HashMap<>();

    //default registered byte serializer types
    static {
        registered.put(ByteSerializerType.DEFAULT.name(), KryoSerializer::new);
        registered.put(ByteSerializerType.KRYO_SERIALIZER.name(), KryoSerializer::new);
        registered.put(ByteSerializerType.ARRAY_STREAM_SERIALIZER.name(), ByteStreamSerializer::new);
    }

    /**
     * Register a new byte serializer supplier
     *
     * @param type the key of the byte serializer supplier
     * @param supplier the byte serializer supplier to register
     */
    public void register(String type, Supplier<ByteSerializer<?>> supplier) {
        registered.put(type, supplier);
    }

    /**
     * Get the default byte serializer
     *
     * @param <T> the object type to serialize
     * @return the byte serializer
     */
    public static <T> ByteSerializer<T> getSerializer() {
        return getSerializer(ByteSerializerType.DEFAULT.name());
    }

    /**
     * Get a byte serializer
     *
     * @param type the type of serializer to retrieve
     * @param <T> the object type to serialize
     * @return the byte serializer
     */
    public static <T> ByteSerializer<T> getSerializer(ByteSerializerType type) {
        return getSerializer(type.name());
    }

    /**
     * Get a byte serializer
     *
     * @param type the type of serializer to retrieve
     * @param <T> the object type to serialize
     * @return the byte serializer
     */
    @SuppressWarnings("unchecked")
    public static <T> ByteSerializer<T> getSerializer(String type) {
        return (ByteSerializer<T>) registered.getOrDefault(type, KryoSerializer::new).get();
    }

    /*
    public static <T> ByteSerializer<T> getSerializer(ByteSerializerType type) {
        switch(type) {
            case ARRAY_STREAM_SERIALIZER:
                return new ByteStreamSerializer<>();
            case KRYO_SERIALIZER:
            default:
                return new KryoSerializer<>();
        }
    }

    public static <T> ByteSerializer<T> getDefault() {
        return getSerializer(ByteSerializerType.DEFAULT);
    }
    */
}
