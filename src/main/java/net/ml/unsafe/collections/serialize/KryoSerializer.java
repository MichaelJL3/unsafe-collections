package net.ml.unsafe.collections.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer<T> implements ByteSerializer<T> {
    private static final ThreadLocal<Kryo> kryoThread = ThreadLocal.withInitial(Kryo::new);

    public KryoSerializer() {}

    public KryoSerializer(Class<T> classType) {
        kryoThread.get().register(classType);
    }

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

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             Input input = new Input(in)) {
            return (T) kryoThread.get().readClassAndObject(input);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
