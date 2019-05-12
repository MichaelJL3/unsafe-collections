package net.ml.unsafe.collections.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo byte serializer
 *
 * @author micha
 * @param <T> the type of object to serialize
 */
@NoArgsConstructor
public final class KryoSerializer<T> implements ByteSerializer<T> {
    private static final ThreadLocal<Kryo> kryoThread = ThreadLocal.withInitial(Kryo::new);

    /**
     * Serialize an object into a byte array
     *
     * @param object the object to serialize
     * @return the serialized byte array
     */
    @Override
    public byte[] serialize(T object) {
        ByteArrayOutputStream bytes;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Output output = new Output(out)) {
            kryoThread.get().writeClassAndObject(output, object);
            bytes = out;
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }

        return bytes.toByteArray();
    }

    /**
     * Deserialize bytes into an object
     *
     * @param bytes the bytes to deserialize
     * @return the deserialized object
     */
    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             Input input = new Input(in)) {
            return (T) kryoThread.get().readClassAndObject(input);
        } catch(IOException ex) {
            return null;
        }
    }
}
