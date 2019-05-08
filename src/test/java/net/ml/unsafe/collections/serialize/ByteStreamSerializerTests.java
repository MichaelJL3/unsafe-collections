package net.ml.unsafe.collections.serialize;

import net.ml.unsafe.collections.serialize.model.Container;
import org.junit.Assert;
import org.junit.Test;

public class ByteStreamSerializerTests {
    private final ByteSerializer<Container<Integer>> serializer = new ByteStreamSerializer<>();

    @Test
    public void serializerTest() {
        Container<Integer> content = new Container<>(1, (short) 2, 3);

        byte[] bytes = serializer.serialize(content);

        Assert.assertNotNull(bytes);
        Assert.assertTrue(bytes.length > 0);
        Assert.assertEquals(content, serializer.deserialize(bytes));
    }
}
