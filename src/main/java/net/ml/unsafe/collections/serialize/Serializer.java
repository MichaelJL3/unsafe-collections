package net.ml.unsafe.collections.serialize;

import java.io.Serializable;

/**
 * Serialize objects of type O into type B
 *
 * @author micha
 * @param <B> the serialized output type
 * @param <O> the input object to serialize
 */
public interface Serializer<B, O extends Serializable> {
    //serialize object
    B serialize(O object);
    //deserialize to get object
    O deserialize(B serial);
}
