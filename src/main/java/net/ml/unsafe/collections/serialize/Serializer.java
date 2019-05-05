package net.ml.unsafe.collections.serialize;

/**
 * Serializer interface
 *
 * Serialize and deserialize objects of one type into another
 *
 * @author micha
 * @param <I> the serialized input type
 * @param <O> the deserialized output type
 */
public interface Serializer<I, O> {
    /**
     * Serialize the input
     *
     * @param input the input to serialize
     * @return the serialized output
     */
    O serialize(I input);

    /**
     * Deserialize the output
     *
     * @param output the output to deserialize
     * @return the deserialized input
     */
    I deserialize(O output);
}
