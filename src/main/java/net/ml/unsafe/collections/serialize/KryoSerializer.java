package net.ml.unsafe.collections.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;

public class KryoSerializer<T> implements ByteSerializer<T> {
    private static final ThreadLocal<Kryo> kryoThread = ThreadLocal.withInitial(Kryo::new);

    public KryoSerializer() {}

    public KryoSerializer(Class<T> classType) {
        kryoThread.get().register(classType);
    }

    @Override
    public byte[] serialize(T object) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             DeflaterOutputStream zipOut = new DeflaterOutputStream(out);
             Output output = new Output(zipOut)) {
            kryoThread.get().writeClassAndObject(output, object);
            return out.toByteArray();
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             DeflaterInputStream zipIn = new DeflaterInputStream(in);
             Input input = new Input(zipIn)) {
            return (T) kryoThread.get().readClassAndObject(input);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
