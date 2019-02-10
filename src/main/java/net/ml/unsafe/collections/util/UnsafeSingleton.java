package net.ml.unsafe.collections.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Create a single global instance of unsafe
 *
 * @author micha
 */
public final class UnsafeSingleton {
    //cannot construct
    private UnsafeSingleton() {}

    private static final String UNSAFE_FIELD = "theUnsafe";
    private static final Unsafe unsafe;

    static {
        try {
            //setup the unsafe
            Field f = Unsafe.class.getDeclaredField(UNSAFE_FIELD);
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get the unsafe
     *
     * @return the unsafe
     */
    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
