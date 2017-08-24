package utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO make the type hold a List<X> instead of specifying concurrent linked queue
public class QueueMap<K, V> implements Map<K, V> {
    private ConcurrentHashMap<K, ConcurrentLinkedQueue<V>> map;

    protected QueueMap() {
        this.map = new ConcurrentHashMap<>();
    }

    protected final void addObject(K key, V value) {
        if (!this.map.containsKey(key)) {
            this.map.put(key, new ConcurrentLinkedQueue<>());
        }

        this.map.get(key).add(value);
    }

    protected final void clearAll(K key) {
        if (!this.map.containsKey(key)) {
            return;
        }

        this.map.remove(key);
    }

    protected final Iterator<V> iterator(K key) {
        if (!map.containsKey(key)) {
            return Collections.emptyIterator();
        }

        return this.map.get(key).iterator();
    }

    protected final boolean hasItems(K key) {
        return this.map.containsKey(key) && !this.map.get(key).isEmpty();

    }

    @NotNull
    protected final V getNext(K key) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Key doesn't exist:" + key.toString());
        }

        return this.map.get(key).poll();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        V value;
        try {
            //noinspection unchecked
            value = (V) o;
        } catch (ClassCastException e) {
            return false;
        }

        for (ConcurrentLinkedQueue<V> q : this.map.values()) {
            if (q.contains(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public V get(Object o) {

        ConcurrentLinkedQueue<V> queue = this.map.get(o);
        if (queue.isEmpty()) {
            return null;
        }

        if (queue.size() == 1) {
            V value = queue.poll();

            //noinspection SuspiciousMethodCalls
            this.map.remove(o);
            return value;
        }

        return queue.poll();
    }

    @Override
    public V put(K k, V v) {
        if (map.containsKey(k)) {
            this.map.get(k).add(v);
            return v;
        }
        return null;
    }

    @Override
    public V remove(Object o) {
        ConcurrentLinkedQueue<V> queue = this.map.remove(o);
        return queue.poll();
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        ArrayList<V> values = new ArrayList<>();
        for (K key : map.keySet()) {
            values.addAll(map.get(key));
        }

        return values;
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
