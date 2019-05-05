package net.ml.unsafe.collections.memory;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ConcurrentMemoryBlockTests {
    @Test
    public void concurrentReadTest() {
        int size = 3;

        try (MemoryBlock<Integer> block = new UnsafeMemoryReferenceBlock<>(size);
             MemoryBlock<Integer> memory = new ReadWriteLockMemoryBlock<>(block)) {
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

    @Test
    public void concurrentWriteTest() {
        int size = 3;

        try (MemoryBlock<Integer> block = new UnsafeMemoryReferenceBlock<>(size);
             MemoryBlock<Integer> memory = new ReadWriteLockMemoryBlock<>(block)) {
            Runnable writer = () -> {
                memory.put(0, 1);
                memory.put(1, 2);
                memory.put(2, 3);
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

    @Test
    public void concurrentReadWriteTest() {
        int size = 3;

        try (MemoryBlock<Integer> block = new UnsafeMemoryReferenceBlock<>(size);
             MemoryBlock<Integer> memory = new ReadWriteLockMemoryBlock<>(block)) {
            memory.put(0, 0);
            memory.put(1, 0);
            memory.put(2, 0);

            Runnable reader = () -> memory.forEach(System.out::println);
            Runnable writer = () -> {
                memory.put(0, 1);
                memory.put(1, 2);
                memory.put(2, 3);
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
