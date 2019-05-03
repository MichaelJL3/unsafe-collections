package net.ml.unsafe.collections;

import net.ml.unsafe.collections.model.Container;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.KryoSerializer;
import org.junit.Assert;
import org.junit.Test;

public class SerializeTest {
    private final ByteSerializer<Container<Integer>> serializer = new KryoSerializer<>();

    @Test
    public void serializeTest() {
        Container<Integer> content = new Container<>();
        content.z = 9;
        content.y = 3;

        byte[] bytes = serializer.serialize(content);
        Assert.assertNotNull(bytes);
        Assert.assertTrue(bytes.length > 0);
        Assert.assertEquals(content, serializer.deserialize(bytes));
    }

    @Test
    public void checkConsistentByteLength() {
        Container<Integer> contentOne = new Container<>();
        Container<Integer> contentTwo = new Container<>();
        contentTwo.y = 5;

        byte[] bytesOne = serializer.serialize(contentOne);
        byte[] bytesTwo = serializer.serialize(contentTwo);
        Assert.assertEquals(bytesOne.length, bytesTwo.length);
    }
}
