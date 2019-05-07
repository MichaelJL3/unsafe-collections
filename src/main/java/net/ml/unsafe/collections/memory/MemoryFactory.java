package net.ml.unsafe.collections.memory;

/**
 * Memory factory
 *
 * @author micha
 */
public final class MemoryFactory {
    private MemoryFactory() {}

    /**
     * Get a memory wrapper
     *
     * @param type the type of memory wrapper to retrieve
     * @return the memory wrapper
     */
    public static Memory getMemory(MemoryType type) {
        switch(type) {
            case UNSAFE:
            default:
                return new UnsafeMemory();
        }
    }

    /**
     * Get the default memory wrapper
     *
     * @return the memory wrapper
     */
    public static Memory getDefault() {
        return getMemory(MemoryType.DEFAULT);
    }
}
