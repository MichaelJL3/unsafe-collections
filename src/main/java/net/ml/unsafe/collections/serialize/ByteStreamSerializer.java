package net.ml.unsafe.collections.serialize;

import java.io.*;

/**
 * ByteStream byte serializer
 *
 * @author micha
 * @param <T> the type of object to serialize
 */
public final class ByteStreamSerializer<T> implements ByteSerializer<T> {
    /**
     * Serialize an object into a byte array
     *
     * @param object the object to serialize
     * @return the serialized byte array
     */
    @Override
    public byte[] serialize(T object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            return null;
        }
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
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bis)) {
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }
}
