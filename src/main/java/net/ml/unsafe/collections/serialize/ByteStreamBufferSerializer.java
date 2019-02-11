package net.ml.unsafe.collections.serialize;

import java.io.*;

/**
 * Byte stream buffered write serializer
 *
 * @author micha
 * @param <O> the object to serialize/deserialize
 */
public class ByteStreamBufferSerializer<O extends Serializable> extends ByteStreamSerializer<O> {
    private static final int DEFAULT_FLUSH_SIZE = 1000;

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private int flushSize;
    private int buffered = 0;

    /**
     * Create a new byte stream
     */
    public ByteStreamBufferSerializer() {
        this(DEFAULT_FLUSH_SIZE);
    }

    /**
     * Create a new byte stream
     *
     * @param flushSize the size to flush on
     */
    public ByteStreamBufferSerializer(int flushSize) {
        this.flushSize = flushSize;
    }

    /**
     * Serialize the object to a byte array
     *
     * @param object the object to serialize
     * @return the byte array representation of the object
     */
    @Override
    public byte[] serialize(O object) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            ++buffered;

            //flush a full buffer
            if (buffered == flushSize) {
                byte[] bytes = bos.toByteArray();
                bos.reset();
                out.flush();
                buffered = 0;
                return bytes;
            }

            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
