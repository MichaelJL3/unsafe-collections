package net.ml.unsafe.collections;

import lombok.extern.slf4j.Slf4j;
import net.ml.unsafe.collections.model.Container;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;
import net.ml.unsafe.collections.serialize.ByteSerializerType;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class SerializeTest {
    private final ByteSerializer<Container<Integer>> serializer = ByteSerializerFactory.getDefaultSerializer();

    @Test
    public void serializeTest() {
        Container<Integer> content = new Container<>(1, (short) 9, 3);

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
        contentOne.y = 11111;

        byte[] bytesOne = serializer.serialize(contentOne);
        byte[] bytesTwo = serializer.serialize(contentTwo);

        log.info("{} = {}", bytesOne.length, bytesTwo.length);

        Assert.assertEquals(bytesOne.length, bytesTwo.length);
    }
}
