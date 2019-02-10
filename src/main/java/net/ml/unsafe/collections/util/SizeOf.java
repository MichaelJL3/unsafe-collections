package net.ml.unsafe.collections.util;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * Get the sizes of objects/types in bytes
 *
 * @author micha
 */
public final class SizeOf {
    private static final Type[] NO_GENERICS = {};

    /**
     * Get the size of an object
     *
     * Only counts the size of all the objects primitive fields
     * Treats arrays as pointers when sizing
     *
     * @param o the object
     * @param generics the types of generic variables associated with the class
     * @return the size of the object
     */
    public static int sizeOf(Object o, Type ... generics) {
        return sizeOf(o.getClass(), generics);
    }

    /**
     * Get the size of a class
     *
     * Only counts the size of all the objects primitive fields
     * Treats arrays as pointers when sizing
     *
     * @param c the class
     * @param generics the types of generic variables associated with the class
     * @return the size of the class
     */
    public static int sizeOf(Class c, Type ... generics) {
        if (c.isPrimitive()) return Primitives.getPrimitiveSize(c);
        if (c.isArray()) return Primitives.PTR;

        int size = 0;
        for (Field field : c.getDeclaredFields()) {
            if (UnsafeSerializer.canSerialize(field)) {
                field.setAccessible(true);

                //check if field is generic
                Optional<Type> generic = Arrays.stream(generics)
                        .filter(field.getGenericType()::equals).findFirst();

                size += generic.isPresent() ?
                        sizeOfGeneric(generic.get()) :
                        sizeOf(field.getType(), genericTypes(field));
            }
        }

        Class s = c.getSuperclass();
        return s == null ? size : size + sizeOf(s);
    }

    /**
     * Get the size of a generic type
     *
     * @param type the generic type
     * @return the size of the generic type
     */
    private static int sizeOfGeneric(Type type) {
        try {
            return sizeOf(Class.forName(type.getTypeName()));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get any generic types associated with the field
     *
     * @param field the field to get the generics for
     * @return the array of generic types
     */
    public static Type[] genericTypes(Field field) {
        try {
            ParameterizedType types = (ParameterizedType) field.getGenericType();
            return types.getActualTypeArguments();
        } catch (ClassCastException ex) {
            return NO_GENERICS;
        }
    }
}
