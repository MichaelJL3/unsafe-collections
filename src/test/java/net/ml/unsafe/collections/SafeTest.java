package net.ml.unsafe.collections;

import lombok.extern.slf4j.Slf4j;
import net.ml.unsafe.collections.memory.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Map;

@Slf4j
public abstract class SafeTest {
    @BeforeClass
    public static void setupMemory() {
        MemoryFactory.register(MemoryType.DEFAULT.name(), () -> new TrackedMemory(new LoggedMemory(new GCMemory())));
    }

    @AfterClass
    public static void checkForLeaks() {
        Memory memory = MemoryFactory.getMemory();
        if (memory instanceof TrackedMemory) {
            TrackedMemory tracked = (TrackedMemory) memory;
            if (tracked.hasMemoryLeaks()) {
                for (Map.Entry<Long, Integer> entry : tracked.memoryMap().entrySet()) {
                    log.error("Leak @{}[{}]", entry.getKey(), entry.getValue());
                    tracked.free(entry.getKey());
                }
            }
        }
    }
}
