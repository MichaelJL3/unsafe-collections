package net.ml.unsafe.collections.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Serialization utility for objects using unsafe
 *
 * @author micha
 */
public final class UnsafeSerializer {
    private static final Unsafe unsafe = UnsafeSingleton.getUnsafe();

    private static final Map<Type, SerializationFunction> storeFunction = new HashMap<>();
    private static final Map<Type, SerializationFunction> loadFunction = new HashMap<>();

    //create the store functions
    static {
        storeFunction.put(Integer.class, (o, f, l) -> {
           unsafe.putInt(l, (int) f.get(o));
           return Primitives.INT;
        });

        storeFunction.put(int.class, (o, f, l) -> {
            unsafe.putInt(l, f.getInt(o));
            return Primitives.INT;
        });
    }

    //create the load functions
    static {
        loadFunction.put(Integer.class, (o, f, l) -> {
           f.set(o, unsafe.getInt(l));
           return Primitives.INT;
        });

        loadFunction.put(int.class, (o, f, l) -> {
            f.setInt(o, unsafe.getInt(l));
            return Primitives.INT;
        });
    }

    /**
     * Store a primitive field into the unsafe memory
     *
     * @param o the object to retrieve the values from
     * @param f the field to write the value of
     * @param address the memory location to save the field to
     * @return the byte offset in memory of the location
     * @throws IllegalAccessException on illegal reflective access
     */
    public static byte store(Object o, Field f, long address) throws IllegalAccessException {
        return storeFunction.get(f.getType()).apply(o, f, address);
    }

    /**
     * Loads a primitive field from unsafe memory
     *
     * @param o the object to put the values in
     * @param f the field to retrieve the value for
     * @param address the memory location to get the value from
     * @return the byte offset in memory of the location
     * @throws IllegalAccessException on illegal reflective access
     */
    public static byte load(Object o, Field f, long address) throws IllegalAccessException {
        return loadFunction.get(f.getType()).apply(o, f, address);
    }

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
    private static long store(Object o, Class c, long address) {
        for (Field field : c.getDeclaredFields()) {
            if (canSerialize(field)) {
                try {
                    field.setAccessible(true);

                    //only store primitives to unsafe memory and recursively search and store fields as needed
                    if (Primitives.isPrimitive(field.getType())) {
                        address += store(o, field, address);
                    } else {
                        address = store(field.get(o), c, address);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        Class s = c.getSuperclass();
        return s == null ? address : store(o, s, address);
    }

    /**
     * Fill in the fields of an object using unsafe memory storage
     *
     * @param o the object to fill in
     * @param address the memory location of the object
     * @param generics any generic parameters associated with the object
     * @return the memory offset of the entire object
     */
    public static long fill(Object o, long address, Type ... generics) {
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

    /**
     * Functional interface with the types used for serializing unsafe primitives
     *
     * @author micha
     */
    private interface SerializationFunction extends TriFunction<Object, Field, Long, Byte> {
        @Override
        Byte apply(Object o, Field f, Long l) throws IllegalAccessException;
    }
}
