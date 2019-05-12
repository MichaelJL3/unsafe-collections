package net.ml.unsafe.collections.map;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import net.ml.unsafe.collections.SafeTest;
import net.ml.unsafe.collections.list.MemoryBlockArrayList;
import net.ml.unsafe.collections.memory.GCMemory;
import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.memory.blocks.ArrayMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.ArrayReferenceMemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;
import net.ml.unsafe.collections.memory.blocks.MemoryBlockBuilder;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class MemoryBlockHashMapTests extends SafeTest {
    @Test
    public void test3() {
        try {
            MemoryBlock<List<Integer>> memory = MemoryBlockBuilder.<List<Integer>>builder()
                    .capacity(1)
                    .build();
            MemoryBlock<Integer> embedded = MemoryBlockBuilder.<Integer>builder()
                    .classType(Integer.class)
                    .capacity(3)
                    .build();

            List<List<Integer>> list = new MemoryBlockArrayList<>(memory);
            List<Integer> innerList = new MemoryBlockArrayList<>(embedded);
            innerList.add(4);
            innerList.add(3);
            innerList.add(6);
            list.add(innerList);
            //list.set(0, innerList);
            List<Integer> test = list.get(0);
            test.forEach(System.out::println);

            embedded.free();
            memory.free();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void test5() {
        try {
            MemoryBlock<Object> memory = ArrayReferenceMemoryBlock.<Object>builder()
                    .capacity(1)
                    .build();

            List<Object> list = new MemoryBlockArrayList<>(memory);
            list.add(new Object());
            list.forEach(System.out::println);

            memory.free();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void test4() {
        try {
            MemoryBlock<MemoryBlock<Integer>> memory = ArrayReferenceMemoryBlock.<MemoryBlock<Integer>>builder()
                    .capacity(1)
                    .build();
            MemoryBlock<Integer> embedded = ArrayMemoryBlock.<Integer>builder()
                    .classSize(Integer.BYTES)
                    .capacity(3)
                    .build();

            List<MemoryBlock<Integer>> list = new MemoryBlockArrayList<>(memory);
            //List<Integer> innerList = new MemoryBlockArrayList<>(embedded);
            list.add(embedded);
            embedded.put(0, 4);
            embedded.put(1, 3);
            embedded.put(2, 6);
            list.set(0, embedded);
            MemoryBlock<Integer> test = list.get(0);
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
