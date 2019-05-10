package net.ml.unsafe.collections.map;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import net.ml.unsafe.collections.list.MemoryBlockArrayList;
import net.ml.unsafe.collections.memory.FakeMemory;
import net.ml.unsafe.collections.memory.MemoryFactory;
import net.ml.unsafe.collections.memory.MemoryType;
import net.ml.unsafe.collections.memory.blocks.ArrayMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.ArrayReferenceMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class MemoryBlockHashMapTests {
    @BeforeClass
    public static void setupMemory() {
        MemoryFactory.register(MemoryType.DEFAULT.name(), FakeMemory::new);
    }

    @Test
    public void test3() {
        try {
            MemoryBlock<List<Integer>> memory = new ArrayReferenceMemoryBlock<>(0);
            MemoryBlock<Integer> embedded = new ArrayMemoryBlock<>(Integer.BYTES, 3);//new ArrayReferenceMemoryBlock<>(0);

            List<List<Integer>> list = new MemoryBlockArrayList<>(memory);
            List<Integer> innerList = new MemoryBlockArrayList<>(embedded);
            list.add(innerList);
            innerList.add(4);
            innerList.add(3);
            innerList.add(6);
            list.set(0, innerList);
            List<Integer> test = list.get(0);
            for (int i = 0; i < test.size(); ++i) {
                System.out.println(test.get(i));
            }
            //test.forEach(System.out::println);

            embedded.free();
            memory.free();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void test() {
        try (MemoryBlock<List<Map.Entry<Integer, Integer>>> memory = new ArrayReferenceMemoryBlock<>(0)) {
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
