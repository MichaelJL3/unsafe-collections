package net.ml.unsafe.collections.memory;

import lombok.extern.slf4j.Slf4j;
import net.ml.unsafe.collections.memory.blocks.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ConcurrentMemoryBlockTests {
    @Test
    public void concurrentReadArrayBlockTest() {
        concurrentReadTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void concurrentWriteArrayBlockTest() {
        concurrentWriteTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void concurrentReadWriteArrayBlockTest() {
        concurrentReadWriteTest(ArrayMemoryBlock.<Integer>builder().classSize(Integer.BYTES).capacity(3).build());
    }

    @Test
    public void concurrentReadArrayReferenceBlockTest() {
        concurrentReadTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void concurrentWriteArrayReferenceBlockTest() {
        concurrentWriteTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void concurrentReadWriteArrayReferenceBlockTest() {
        concurrentReadWriteTest(ArrayReferenceMemoryBlock.<Integer>builder().capacity(3).build());
    }

    @Test
    public void concurrentReadLinkedBlockTest() {
        concurrentReadTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void concurrentWriteLinkedBlockTest() {
        concurrentWriteTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void concurrentReadWriteLinkedBlockTest() {
        concurrentReadWriteTest(LinkedMemoryBlock.<Integer>builder().classSize(Integer.BYTES).build());
    }

    @Test
    public void concurrentReadLinkedReferenceBlockTest() {
        concurrentReadTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void concurrentWriteLinkedReferenceBlockTest() {
        concurrentWriteTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    @Test
    public void concurrentReadWriteLinkedReferenceBlockTest() {
        concurrentReadWriteTest(LinkedReferenceMemoryBlock.<Integer>builder().build());
    }

    private void concurrentReadTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = new ReadWriteLockMemoryBlock<>(block)) {
            memory.put(0, 1);
            memory.put(1, 2);
            memory.put(2, 3);

            Runnable reader = () -> memory.forEach(System.out::println);

            CompletableFuture readerOne = CompletableFuture.runAsync(reader);
            CompletableFuture readerTwo = CompletableFuture.runAsync(reader);
            readerOne.get();
            readerTwo.get();
        } catch (InterruptedException | ExecutionException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void concurrentWriteTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = new ReadWriteLockMemoryBlock<>(block)) {
            memory.put(0, 1);
            memory.put(1, 2);
            memory.put(2, 3);

            Runnable writer = () -> {
                memory.replace(0, 1);
                memory.replace(1, 2);
                memory.replace(2, 3);
            };

            CompletableFuture writerOne = CompletableFuture.runAsync(writer);
            CompletableFuture writerTwo = CompletableFuture.runAsync(writer);
            writerOne.get();
            writerTwo.get();

            Assert.assertEquals(new Integer(1), memory.get(0));
            Assert.assertEquals(new Integer(2), memory.get(1));
            Assert.assertEquals(new Integer(3), memory.get(2));
        } catch (InterruptedException | ExecutionException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void concurrentReadWriteTest(MemoryBlock<Integer> block) {
        try (MemoryBlock<Integer> memory = new ReadWriteLockMemoryBlock<>(block)) {
            memory.put(0, 0);
            memory.put(1, 0);
            memory.put(2, 0);

            Runnable reader = () -> memory.forEach(System.out::println);
            Runnable writer = () -> {
                memory.replace(0, 1);
                memory.replace(1, 2);
                memory.replace(2, 3);
            };

            CompletableFuture readerOne = CompletableFuture.runAsync(reader);
            CompletableFuture writerOne = CompletableFuture.runAsync(writer);
            CompletableFuture readerTwo = CompletableFuture.runAsync(reader);
            readerOne.get();
            writerOne.get();
            readerTwo.get();

            Assert.assertEquals(new Integer(1), memory.get(0));
            Assert.assertEquals(new Integer(2), memory.get(1));
            Assert.assertEquals(new Integer(3), memory.get(2));
        } catch (InterruptedException | ExecutionException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
