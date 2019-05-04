package net.ml.unsafe.collections.util;

import java.util.HashMap;
import java.util.Map;

public class SizeUtil {
    private SizeUtil() {}

    private static final Map<String, Integer> memoizedSize = new HashMap<>();
    public static final byte WORDSIZE = 8;

    public static <T> int sizeOf(T o) {
        String className = o.getClass().getName();
        memoizedSize.putIfAbsent(className, calculateSize(o));
        return memoizedSize.get(className);
    }

    public static <T> void registerSize(Class<T> type, int size) {
        memoizedSize.put(type.getName(), size);
    }

    public static int alignBy(int size, byte alignment) {
        if (size < 0 || alignment <= 0) throw new IllegalArgumentException();

        int remaining = size % alignment;
        return size + (remaining > 0 ? alignment - remaining : 0);
    }

    private static <T> int calculateSize(T o) {
        return 0;
    }
}
