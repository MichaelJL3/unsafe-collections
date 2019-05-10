//package net.ml.unsafe.collections.memory;
//
//import lombok.extern.slf4j.Slf4j;
//import net.ml.unsafe.collections.memory.blocks.*;
//import org.junit.Test;
//
//@Slf4j
//public class MemoryBlockIteratorTests {
//    @Test
//    public void iterateArrayMemoryBlock() {
//        iterateTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
//    }
//
//    @Test
//    public void iteratePartialFilledArrayMemoryBlock() {
//        iteratePartialFilledTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
//    }
//
//    @Test
//    public void iterateLinkedMemoryBlock() {
//        iterateTest(new LinkedMemoryBlock<>(Integer.BYTES));
//    }
//
//    @Test
//    public void iterateArrayReferenceMemoryBlock() {
//        iterateTest(new ArrayReferenceMemoryBlock<>(3));
//    }
//
//    @Test
//    public void iteratePartialFilledArrayReferenceMemoryBlock() {
//        iteratePartialFilledTest(new ArrayReferenceMemoryBlock<>(3));
//    }
//
//    @Test
//    public void iterateLinkedReferenceMemoryBlock() {
//        iterateTest(new LinkedReferenceMemoryBlock<>());
//    }
//
//    private void iterateTest(MemoryBlock<Integer> block) {
//        try (MemoryBlock<Integer> memory = block) {
//            memory.put(0, 1);
//            memory.put(1, 2);
//            memory.put(2, 3);
//            memory.forEach(i -> log.info("{}", i));
//        }
//    }
//
//    private void iteratePartialFilledTest(MemoryBlock<Integer> block) {
//        try (MemoryBlock<Integer> memory = block) {
//            memory.put(0, 1);
//            memory.forEach(i -> log.info("{}", i));
//        }
//    }
//}
