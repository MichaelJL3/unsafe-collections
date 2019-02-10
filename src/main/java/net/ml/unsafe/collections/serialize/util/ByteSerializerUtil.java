package net.ml.unsafe.collections.serialize.util;

import net.ml.unsafe.collections.util.Primitives;
import net.ml.unsafe.collections.util.ReflectionUtil;
import net.ml.unsafe.collections.util.SizeOf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public final class ByteSerializerUtil {
    /**
     * Stores an object into an unsafe memory location
     *
     * @param o the object to store
     * @param address the memory location
     * @return the addresses offset
     */
    public static long store(Object o, long address) {
        return store(o, o.getClass(), address);
    }

    /**
     * Load the object type from an unsafe memory location
     *
     * @param c the class type to load
     * @param address the memory location
     * @return the loaded object
     */
    public static Object load(Class c, long address) {
        Object o = ReflectionUtil.create(c);
        fill(o, address);
        return o;
    }

    /**
     * Stores an object into an unsafe memory location
     *
     * @param o the object to store
     * @param c the class scheme to use to fill in
     * @param address the memory location
     * @return the address offset
     */
    private static long store(Object o, Class c, byte[] bytes, long address) {
        for (Field field : c.getDeclaredFields()) {
            if (canSerialize(field)) {
                try {
                    field.setAccessible(true);

                    //only store primitives to unsafe memory and recursively search and store fields as needed
                    if (Primitives.isPrimitive(field.getType())) {
                        int size = Primitives.getPrimitiveSize(field.getType());
                        Object p = Primitives.getPrimitive(field.getType());
                        for (int i = 0; i < size; ++i, ++address) {
                            bytes[i] = (byte) p;
                        }
                        address += store(o, field, address);
                    } else {
                        address = store(field.get(o), c, bytes, address);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        Class s = c.getSuperclass();
        return s == null ? address : store(o, s, bytes, address);
    }

    /**
     * Fill in the fields of an object using unsafe memory storage
     *
     * @param o the object to fill in
     * @param address the memory location of the object
     * @param generics any generic parameters associated with the object
     * @return the memory offset of the entire object
     */
    public static long fill(Object o, long address, Type... generics) {
        return fill(o, o.getClass(), address, generics);
    }

    /**
     * Fill in the fields of an object using unsafe memory storage
     *
     * @param o the object to fill in
     * @param c the class scheme to use to fill in
     * @param address the memory location of the object
     * @param generics any generic parameters associated with the object
     * @return the memory offset of the entire object
     */
    public static long fill(Object o, Class c, long address, Type ... generics) {
        for (Field field : c.getDeclaredFields()) {
            if (canSerialize(field)) {
                try {
                    field.setAccessible(true);

                    //only get primitives from unsafe memory and recursively search and create objects as needed
                    if (Primitives.isPrimitive(field.getType())) {
                        address += load(o, field, address);
                    } else {
                        //check if field is generic
                        Optional<Type> generic = Arrays.stream(generics)
                                .filter(field.getGenericType()::equals).findFirst();

                        //create nested object
                        Object nested = generic.isPresent() ?
                                ReflectionUtil.create(Class.forName(generic.get().getTypeName())) :
                                ReflectionUtil.create(field.getType());

                        //populate nested object
                        address = fill(nested, address, SizeOf.genericTypes(field));
                        field.set(o, nested);
                    }
                } catch (IllegalAccessException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        Class s = c.getSuperclass();
        return s == null ? address : fill(o, s, address);
    }

    /**
     * Check if a field can be serialized
     *
     * @param field the field
     * @return whether or not the field is serializable
     */
    public static boolean canSerialize(Field field) {
        int modifiers = field.getModifiers();
        return !(Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers));
    }
}
