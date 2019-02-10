package net.ml.unsafe.collections.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions for primitive checking
 *
 * @author micha
 */
public final class Primitives {
    //assuming 64 based architecture
    public static final byte PTR = 8;
    public static final byte BYTE = 1;
    public static final byte CHAR = 2;
    public static final byte SHORT = 2;
    public static final byte INT = 4;
    public static final byte LONG = 8;
    public static final byte FLOAT = 4;
    public static final byte DOUBLE = 8;
    public static final byte BOOLEAN = 1;

    private static final Map<Class, Object> wrappers = new HashMap<>();
    private static final Map<Class, Byte> bytes = new HashMap<>();

    //load the basic primitive type sizes in bytes
    static {
        bytes.put(byte.class, BYTE);
        bytes.put(short.class, SHORT);
        bytes.put(boolean.class, BOOLEAN);
        bytes.put(char.class, CHAR);
        bytes.put(int.class, INT);
        bytes.put(long.class, LONG);
        bytes.put(float.class, FLOAT);
        bytes.put(double.class, DOUBLE);

        bytes.put(Byte.TYPE, BYTE);
        bytes.put(Short.TYPE, SHORT);
        bytes.put(Boolean.TYPE, BOOLEAN);
        bytes.put(Character.TYPE, CHAR);
        bytes.put(Integer.TYPE, INT);
        bytes.put(Long.TYPE, LONG);
        bytes.put(Float.TYPE, FLOAT);
        bytes.put(Double.TYPE, DOUBLE);

        bytes.put(Byte.class, BYTE);
        bytes.put(Short.class, SHORT);
        bytes.put(Boolean.class, BOOLEAN);
        bytes.put(Character.class, CHAR);
        bytes.put(Integer.class, INT);
        bytes.put(Long.class, LONG);
        bytes.put(Float.class, FLOAT);
        bytes.put(Double.class, DOUBLE);
    }

    //load the basic primitive type defaults
    static {
        wrappers.put(byte.class, 0);
        wrappers.put(short.class, 0);
        wrappers.put(boolean.class, 0);
        wrappers.put(char.class, 0);
        wrappers.put(int.class, 0);
        wrappers.put(long.class, 0);
        wrappers.put(float.class, 0);
        wrappers.put(double.class, 0);

        wrappers.put(Byte.TYPE, 0);
        wrappers.put(Short.TYPE, 0);
        wrappers.put(Boolean.TYPE, 0);
        wrappers.put(Character.TYPE, 0);
        wrappers.put(Integer.TYPE, 0);
        wrappers.put(Long.TYPE, 0);
        wrappers.put(Float.TYPE, 0);
        wrappers.put(Double.TYPE, 0);

        wrappers.put(Byte.class, 0);
        wrappers.put(Short.class, 0);
        wrappers.put(Boolean.class, 0);
        wrappers.put(Character.class, 0);
        wrappers.put(Integer.class, 0);
        wrappers.put(Long.class, 0);
        wrappers.put(Float.class, 0);
        wrappers.put(Double.class, 0);
    }

    /**
     * Check if class type is a primitive wrapper class
     *
     * @param c the class
     * @return whether or not it is a primitive wrapper
     */
    public static boolean isPrimitive(Class c) {
        return wrappers.containsKey(c);
    }

    /**
     * Create a new default object for the primitive type
     *
     * @param c the class
     * @return the primitive object
     */
    public static Object getPrimitive(Class c) {
        return wrappers.get(c);
    }

    /**
     * Get the size of the primitive
     *
     * @param c the class
     * @return the size of the primitive
     */
    public static byte getPrimitiveSize(Class c) {
        return bytes.get(c);
    }
}
