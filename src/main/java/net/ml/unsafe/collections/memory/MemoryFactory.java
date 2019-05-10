package net.ml.unsafe.collections.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Memory factory
 *
 * @author micha
 */
public final class MemoryFactory {
    private MemoryFactory() {}

    private static final Map<String, Supplier<Memory>> registered = new HashMap<>();

    //default registered memory types
    static {
        registered.put(MemoryType.UNSAFE.name(), UnsafeMemory::new);
        registered.put(MemoryType.DEFAULT.name(), UnsafeMemory::new);
    }

    /**
     * Register a new memory supplier
     *
     * @param type the key of the memory supplier
     * @param supplier the memory supplier to register
     */
    public static void register(String type, Supplier<Memory> supplier) {
        registered.put(type, supplier);
    }

    /**
     * Get the default memory wrapper
     *
     * @return the memory wrapper
     */
    public static Memory getMemory() {
        return getMemory(MemoryType.DEFAULT);
    }

    /**
     * Get a memory wrapper
     *
     * @param type the type of memory wrapper to retrieve
     * @return the memory wrapper
     */
    public static Memory getMemory(String type) {
        return registered.getOrDefault(type, UnsafeMemory::new).get();
    }

    /**
     * Get a memory wrapper
     *
     * @param type the type of memory wrapper to retrieve
     * @return the memory wrapper
     */
    public static Memory getMemory(MemoryType type) {
        return getMemory(type.name());
    }
}
