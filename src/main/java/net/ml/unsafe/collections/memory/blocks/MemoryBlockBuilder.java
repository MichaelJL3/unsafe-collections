package net.ml.unsafe.collections.memory.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.ml.unsafe.collections.memory.Memory;
import net.ml.unsafe.collections.serialize.ByteSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemoryBlockBuilder {
    private static final Map<Class, Integer> primitives = new HashMap<>();

    static {
        primitives.put(int.class, Integer.BYTES);
        primitives.put(char.class, Character.BYTES);
        primitives.put(boolean.class, 1);
        primitives.put(long.class, Long.BYTES);
        primitives.put(double.class, Double.BYTES);
        primitives.put(float.class, Float.BYTES);
        primitives.put(short.class, Short.BYTES);
        primitives.put(Integer.class, Integer.BYTES);
        primitives.put(Character.class, Character.BYTES);
        primitives.put(Boolean.class, 1);
        primitives.put(Long.class, Long.BYTES);
        primitives.put(Double.class, Double.BYTES);
        primitives.put(Float.class, Float.BYTES);
        primitives.put(Short.class, Short.BYTES);
    }

    private static <T> boolean isPrimitive(Class<T> type) {
        return primitives.containsKey(type);
    }

    private static <T> int sizeOfPrimitive(Class<T> type) {
        return primitives.get(type);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder<T> {
        transient private int classSize;
        transient private int capacity;
        transient private Class<T> classType;
        transient private boolean linked;
        transient private boolean concurrent;
        transient private boolean bounded;
        transient private Memory memory;
        transient private ByteSerializer<T> serializer;

        public MemoryBlock<T> build() {
            if (Objects.nonNull(classType) && isPrimitive(classType))
                classSize = sizeOfPrimitive(classType);

            MemoryBlock<T> block = linked ?
                    classSize != 0 ?
                        createLinkedBlock() :
                        createLinkedReferenceBlock() :
                    classSize != 0 ?
                        createArrayBlock() :
                        createArrayReferenceBlock();

            if (bounded) block = new BoundedMemoryBlock<>(block);
            if (concurrent) block = new ReadWriteLockMemoryBlock<>(block);

            return block;
        }

        private LinkedReferenceMemoryBlock<T> createLinkedReferenceBlock() {
            return LinkedReferenceMemoryBlock.<T>builder()
                .capacity(capacity)
                .serializer(serializer)
                .memory(memory)
                .build();
        }

        private ArrayReferenceMemoryBlock<T> createArrayReferenceBlock() {
            return ArrayReferenceMemoryBlock.<T>builder()
                .capacity(capacity)
                .serializer(serializer)
                .memory(memory)
                .build();
        }

        private ArrayMemoryBlock<T> createArrayBlock() {
            return ArrayMemoryBlock.<T>builder()
                .classSize(classSize)
                .capacity(capacity)
                .serializer(serializer)
                .memory(memory)
                .build();
        }

        private LinkedMemoryBlock<T> createLinkedBlock() {
            return LinkedMemoryBlock.<T>builder()
                .classSize(classSize)
                .capacity(capacity)
                .serializer(serializer)
                .memory(memory)
                .build();
        }
    }
}
