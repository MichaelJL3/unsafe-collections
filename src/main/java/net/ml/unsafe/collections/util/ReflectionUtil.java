package net.ml.unsafe.collections.util;

/**
 * Utilities for reflections
 *
 * @author micha
 */
public final class ReflectionUtil {
    /**
     * Create an instance of the given class
     * Uses special constructors with default values to create primitive wrappers
     *
     * Only works when there is a default constructor
     *
     * @param c the class to create an instance of
     * @return an instance if the class
     */
    public static Object create(Class c) {
        if (c.isArray()) throw new RuntimeException("Cannot load array type");

        //create primitives using special constructor
        if (Primitives.isPrimitive(c)) {
            return Primitives.getPrimitive(c);
        } else {
            try {
                //create object using default constructor
                return c.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
