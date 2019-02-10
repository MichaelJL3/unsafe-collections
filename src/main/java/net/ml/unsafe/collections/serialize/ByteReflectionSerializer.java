package net.ml.unsafe.collections.serialize;

import java.io.Serializable;

public class ByteReflectionSerializer<O extends Serializable> implements ByteSerializer<O> {
    private final Class<O> classType;

    public ByteReflectionSerializer(Class<O> classType) {
        this.classType = classType;
    }

    @Override
    public byte[] serialize(O object) {

    }

    @Override
    public O deserialize(byte[] serial) {

    }
}
