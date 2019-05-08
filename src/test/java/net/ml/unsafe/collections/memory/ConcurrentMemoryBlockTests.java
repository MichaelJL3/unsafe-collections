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
        concurrentReadTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
    }

    @Test
    public void concurrentWriteArrayBlockTest() {
        concurrentWriteTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
    }

    @Test
    public void concurrentReadWriteArrayBlockTest() {
        concurrentReadWriteTest(new ArrayMemoryBlock<>(Integer.BYTES, 3));
    }

    @Test
    public void concurrentReadArrayReferenceBlockTest() {
        concurrentReadTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void concurrentWriteArrayReferenceBlockTest() {
        concurrentWriteTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void concurrentReadWriteArrayReferenceBlockTest() {
        concurrentReadWriteTest(new ArrayReferenceMemoryBlock<>(3));
    }

    @Test
    public void concurrentReadLinkedBlockTest() {
        concurrentReadTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void concurrentWriteLinkedBlockTest() {
        concurrentWriteTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void concurrentReadWriteLinkedBlockTest() {
        concurrentReadWriteTest(new LinkedMemoryBlock<>(Integer.BYTES));
    }

    @Test
    public void concurrentReadLinkedReferenceBlockTest() {
        concurrentReadTest(new LinkedReferenceMemoryBlock<>());
    }

    @Test
    public void concurrentWriteLinkedReferenceBlockTest() {
        concurrentWriteTest(new LinkedReferenceMemoryBlock<>());
    }

    @Test
    public void concurrentReadWriteLinkedReferenceBlockTest() {
        concurrentReadWriteTest(new LinkedReferenceMemoryBlock<>());
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
