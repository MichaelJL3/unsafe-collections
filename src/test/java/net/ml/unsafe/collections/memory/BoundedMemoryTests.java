//package net.ml.unsafe.collections.memory;
//
//import net.ml.unsafe.collections.memory.blocks.*;
//import org.junit.Test;
//
//public class BoundedMemoryTests {
//    @Test
//    public void inBoundsArrayBlockTest() {
//        inBoundsTest(new ArrayMemoryBlock<>(Integer.BYTES));
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void outOfBoundsArrayBlockTest() {
//        outOfBoundsTest(new ArrayMemoryBlock<>(Integer.BYTES, 0));
//    }
//
//    @Test
//    public void inBoundsLinkedBlockTest() {
//        inBoundsTest(new LinkedMemoryBlock<>(Integer.BYTES));
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void outOfBoundsLinkedBlockTest() {
//        outOfBoundsTest(new LinkedMemoryBlock<>(Integer.BYTES));
//    }
//
//    @Test
//    public void inBoundsArrayReferenceBlockTest() {
//        inBoundsTest(new ArrayReferenceMemoryBlock<>(1));
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void outOfBoundsArrayReferenceBlockTest() {
//        outOfBoundsTest(new ArrayReferenceMemoryBlock<>(0));
//    }
//
//    @Test
//    public void inBoundsLinkedReferenceBlockTest() {
//        inBoundsTest(new LinkedReferenceMemoryBlock<>());
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void outOfBoundsLinkedReferenceBlockTest() {
//        outOfBoundsTest(new LinkedReferenceMemoryBlock<>());
//    }
//
//    private void inBoundsTest(MemoryBlock<Integer> block) {
//        try (MemoryBlock<Integer> memory = new BoundedMemoryBlock<>(block)) {
//            memory.put(0, 1);
//            memory.get(0);
//        }
//    }
//
//    private void outOfBoundsTest(MemoryBlock<Integer> block) {
//        try (MemoryBlock<Integer> memory = new BoundedMemoryBlock<>(block)) {
//            memory.get(3);
//        }
//    }
//}
