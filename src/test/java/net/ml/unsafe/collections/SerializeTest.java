package net.ml.unsafe.collections;

import net.ml.unsafe.collections.model.Container;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import net.ml.unsafe.collections.serialize.ByteSerializerFactory;
import net.ml.unsafe.collections.serialize.ByteSerializerType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SerializeTest {
    @Test
    public void serializeTest() {
        ByteSerializer<Container<Integer>> serializer =
                ByteSerializerFactory.getSerializer(ByteSerializerType.DEFAULT);

        Container<Integer> content = new Container<>(1, (short) 9, 3);

        byte[] bytes = serializer.serialize(content);

        Container<Integer> copyContainer = serializer.deserialize(bytes);
        Assert.assertEquals(content, copyContainer);
    }

    @Test
    public void deserializeTest() {
        List<Container<Integer>> list = new UnsafeArrayList<>();

        list.add(new Container<>(1, (short) 2, 3));
        list.add(new Container<>(3, (short) 3, 3));

        list.forEach(System.out::println);
    }
}
