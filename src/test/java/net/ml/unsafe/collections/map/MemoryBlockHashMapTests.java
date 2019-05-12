package net.ml.unsafe.collections.map;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import net.ml.unsafe.collections.SafeTest;
import net.ml.unsafe.collections.list.MemoryBlockArrayList;
import net.ml.unsafe.collections.memory.*;
import net.ml.unsafe.collections.memory.blocks.ArrayMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.ArrayReferenceMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class MemoryBlockHashMapTests extends SafeTest {
    @Test
    public void test3() {
        try {
            MemoryBlock<List<Integer>> memory = ArrayReferenceMemoryBlock.<List<Integer>>builder().build();
            MemoryBlock<Integer> embedded = ArrayMemoryBlock.<Integer>builder()
                    .classSize(Integer.BYTES)
                    .capacity(3)
                    .build();

            List<List<Integer>> list = new MemoryBlockArrayList<>(memory);
            List<Integer> innerList = new MemoryBlockArrayList<>(embedded);
            list.add(innerList);
            innerList.add(4);
            innerList.add(3);
            innerList.add(6);
            list.set(0, innerList);
            List<Integer> test = list.get(0);
            test.forEach(System.out::println);

            embedded.free();
            memory.free();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void test() {
        try (MemoryBlock<List<Map.Entry<Integer, Integer>>> memory =
                     ArrayReferenceMemoryBlock.<List<Map.Entry<Integer, Integer>>>builder().build()) {
            Map<Integer, Integer> map = new MemoryBlockHashMap<>(memory);

            final Stopwatch watch = Stopwatch.createStarted();
            //IntStream.range(0, 2).forEach(i -> map.put(i, i));
            map.put(0, 0);
            map.put(1, 1);
            IntStream.range(0, 1).forEach(i -> System.out.println(map.get(i)));
            watch.stop();
            //System.out.println(map.get(0));
            log.info("{}", watch);
        }
    }

    @Test
    public void test2() {
        Map<Integer, Integer> map = new HashMap<>();

        final Stopwatch watch = Stopwatch.createStarted();
        map.put(0, 1);
        IntStream.range(0, 100).forEach(i -> System.out.println(map.get(0)));
        watch.stop();
        System.out.println(watch);
    }
}
