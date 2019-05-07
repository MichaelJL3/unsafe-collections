package net.ml.unsafe.collections.serialize;

import net.ml.unsafe.collections.memory.blocks.models.Reference;

import java.nio.ByteBuffer;

/**
 * Serializer for references
 *
 * @author micha
 */
public final class ReferenceSerializer implements ByteSerializer<Reference> {
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(Reference.size());

    /**
     * Serialize a reference to a byte array
     *
     * @param ref the reference to serialize
     * @return the bytes
     */
    @Override
    public byte[] serialize(Reference ref) {
        byteBuffer.clear();
        byteBuffer.putLong(0, ref.addr);
        byteBuffer.putInt(Reference.WORD_SIZE, ref.length);
        return byteBuffer.array();
    }

    /**
     * Deserialize a byte array to a reference object
     *
     * @param serial the bytes to deserialize
     * @return the reference
     */
    @Override
    public Reference deserialize(byte[] serial) {
        byteBuffer.clear();
        byteBuffer.put(serial);
        return new Reference(byteBuffer.getLong(0), byteBuffer.getInt(Reference.WORD_SIZE));
    }
}
