package net.ml.unsafe.collections.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ml.unsafe.collections.memory.blocks.MemoryBlock;

import java.util.*;

public class MemoryBlockHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private final MemoryBlock<List<Entry<K,V>>> memory;
    private final float loadFactor;

    private Set<Map.Entry<K, V>> entrySet;
    private Set<K> keys;
    private Collection<V> values;
    private int size = 0;
    private int modifications = 0;
    private int threshold;

    public MemoryBlockHashMap(MemoryBlock<List<Entry<K,V>>> memory) {
        this(memory, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MemoryBlockHashMap(MemoryBlock<List<Entry<K,V>>> memory, int initialCapacity) {
        this(memory, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public MemoryBlockHashMap(MemoryBlock<List<Entry<K,V>>> memory, int initialCapacity, float loadFactor) {
        this.memory = memory;
        resize();

        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    private static int tableSizeFor(int cap) {
        --cap;
        cap |= cap >>> 1;
        cap |= cap >>> 2;
        cap |= cap >>> 4;
        cap |= cap >>> 8;
        cap |= cap >>> 16;
        return (cap < 0) ? 1 : (cap >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : cap + 1;
    }

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public V get(Object key) {
        Entry<K,V> entry = getEntry(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V getOrDefault(Object key, V value) {
        V retrieved = get(key);
        return retrieved == null ? value : retrieved;
    }

    @Override
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        putMapEntries(map);
    }

    @Override
    public V remove(Object key) {
        Entry<K,V> removed = removeEntry(key, null);
        return removed == null ? null : removed.getValue();
    }

    @Override
    public boolean remove(Object key, Object value) {
        return removeEntry(key, value) != null;
    }

    @Override
    public V replace(K key, V value) {
        return replaceVal(key, null, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return replaceVal(key, oldValue, newValue) != null;
    }

    @Override
    public void clear() {
        memory.free();
        entrySet = null;
        values = null;
        keys = null;
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (List<Entry<K,V>> bucket : memory) {
            for (Entry<K,V> entry : bucket) {
                if (Objects.equals(entry.getValue(), value)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Set<K> keySet() {
        return keys == null ? (keys = new KeySet()) : keys;
    }

    @Override
    public Collection<V> values() {
        return values == null ? (values = new Values()) : values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return entrySet == null ? (entrySet = new EntrySet()) : entrySet;
    }

    private void resize() {
        int size = size();
        int newCap;

        if (size > 0) {
            if (size >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return;
            }
            else if ((newCap = size << 1) < MAXIMUM_CAPACITY &&
                    size >= DEFAULT_INITIAL_CAPACITY)
                threshold <<= 1; // double threshold
        }
        else if (threshold > 0) // initial capacity was placed in threshold
            newCap = threshold;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            threshold = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (threshold == 0) {
            float ft = (float) newCap * loadFactor;
            threshold = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
                    (int) ft : Integer.MAX_VALUE);
        }

        memory.realloc(newCap);
    }

    private V replaceVal(K key, V oldVal, V newVal) {
        List<Entry<K,V>> bucket = getBucket(hash(key));
        Entry<K,V> node;

        for (int i = 0; i < bucket.size(); ++i) {
            node = bucket.get(i);
            if (Objects.equals(node.getKey(), key) &&
                (oldVal == null || Objects.equals(node.getValue(), oldVal))
            ) {
                V old = node.getValue();
                node.setValue(newVal);
                bucket.set(i, node);
                return old;
            }
        }

        return null;
    }

    private void putMapEntries(Map<? extends K, ? extends V> map) {
        int mapSize = map.size();

        if (mapSize > threshold) resize();

        for (Entry<? extends K, ? extends V> node : map.entrySet()) {
            put(node.getKey(), node.getValue());
        }
    }

    private V putVal(K key, V value, boolean onlyIfAbsent) {
        List<Entry<K,V>> bucket = getBucket(hash(key));
        Entry<K,V> node;

        for (int i = 0; i < bucket.size(); ++i) {
            node = bucket.get(i);
            if (Objects.equals(node.getKey(), key)) {
                V old = node.getValue();
                if (!onlyIfAbsent) {
                    node.setValue(value);
                    bucket.set(i, node);
                }
                return old;
            }
        }

        bucket.add(new Node(key, value));
        ++modifications;
        if (++size > threshold) resize();
        return null;
    }

    private Entry<K,V> removeEntry(Object key, Object value) {
        List<Entry<K,V>> bucket = getBucket(hash(key));
        Entry<K,V> node;

        for (int i = 0; i < bucket.size(); ++i) {
            node = bucket.get(i);
            if (Objects.equals(node.getKey(), key) &&
                (value == null || Objects.equals(node.getValue(), value))) {
                --size;
                return bucket.remove(i);
            }
        }

        return null;
    }

    private Entry<K,V> getEntry(Object key) {
        for (Entry<K,V> node : getBucket(hash(key))) {
            if (Objects.equals(node.getKey(), key)) return node;
        }

        return null;
    }

    private List<Entry<K,V>> getBucket(int hash) {
        return memory.get((size() - 1) & hash);
    }

    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Getter
    @AllArgsConstructor
    private final class Node implements Entry<K,V> {
        private final K key;
        private V value;

        @Override
        public final V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o instanceof Entry) {
                Entry<?,?> e = (Entry<?,?>) o;
                return (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
    }

    private final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            MemoryBlockHashMap.this.clear();
        }

        @Override
        public int size() {
            return size;
        }
    }

    private final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Entry)) return false;

            Entry<?,?> e = (Entry<?,?>) o;
            Object key = e.getKey();
            Entry<K,V> candidate = getEntry(key);
            return candidate != null && candidate.equals(e);
        }

        @Override
        public final boolean remove(Object o) {
            if (!(o instanceof Entry)) return false;

            Entry<?,?> e = (Entry<?,?>) o;
            Entry<?,?> removed = removeEntry(e.getKey(), e.getValue());
            return Objects.equals(removed, e);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            MemoryBlockHashMap.this.clear();
        }
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public final boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public final boolean remove(Object o) {
            return removeEntry(o, null) != null;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            MemoryBlockHashMap.this.clear();
        }
    }

    abstract class HashIterator {
        List<Entry<K,V>> bucket;
        Entry<K,V> next;        // next entry to return
        Entry<K,V> current;     // current entry
        final int buckets = memory.size();
        int expectedModCount = modifications;
        int bucketIndex = 0;
        int listIndex = 0;

        HashIterator() {
            if (buckets > 0) bucket = memory.get(0);
            findNext();
        }

        public final boolean hasNext() {
            return next != null;
        }

        private void findNext() {
            int bucketSize;

            for (; bucketIndex < buckets;) {
                bucketSize = bucket == null ? 0 : bucket.size();

                for (; listIndex < bucketSize; ++listIndex) {
                    next = bucket.get(listIndex);
                    if (next != null) break;
                }

                bucket = memory.get(++bucketIndex);
                listIndex = 0;
            }
        }

        final Entry<K,V> nextNode() {
            current = next;
            if (modifications != expectedModCount)
                throw new ConcurrentModificationException();
            if (current == null)
                throw new NoSuchElementException();

            findNext();
            return current;
        }

        public final void remove() {
            if (current == null)
                throw new IllegalStateException();
            if (modifications != expectedModCount)
                throw new ConcurrentModificationException();

            removeEntry(current.getKey(), null);
            current = null;
            expectedModCount = modifications;
        }
    }

    final class KeyIterator extends HashIterator implements Iterator<K> {
        public final K next() { return nextNode().getKey(); }
    }

    final class ValueIterator extends HashIterator implements Iterator<V> {
        public final V next() { return nextNode().getValue(); }
    }

    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }
}
