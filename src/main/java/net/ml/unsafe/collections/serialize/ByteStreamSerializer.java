package net.ml.unsafe.collections.serialize;

import java.io.*;

/**
 * Byte stream serializer
 *
 * @author micha
 * @param <O> the object to serialize/deserialize
 */
public class ByteStreamSerializer<O extends Serializable> implements ByteSerializer<O> {
    /**
     * Serialize the object to a byte array
     *
     * @param object the object to serialize
     * @return the byte array representation of the object
     */
    @Override
    public byte[] serialize(O object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Deserialize the bytes into an object
     *
     * @param serial the bytes to deserialize
     * @return the object held in bytes
     */
    @Override
    @SuppressWarnings("unchecked")
    public O deserialize(byte[] serial) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(serial)) {
            ObjectInputStream in = new ObjectInputStream(bis);
            return (O) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }
}
