package net.ml.unsafe.collections;

import net.ml.unsafe.collections.model.Container;
import net.ml.unsafe.collections.serialize.ByteSerializer;
import org.junit.Assert;
import org.junit.Test;

public class SerializeTest {
    @Test
    public void serializeTest() {
        Container<Integer> content = new Container<>();
        content.z = 9;
        content.y = 3;

        byte[] bytes = ByteSerializer.serialize(content);
        Assert.assertEquals(content, ByteSerializer.deserialize(bytes));
    }

    @Test
    public void deserializeTest() {

    }
}
