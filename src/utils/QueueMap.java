package utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueMap<K, V> {
    private ConcurrentHashMap<K, ConcurrentLinkedQueue<V>> map;

    protected QueueMap() {
        this.map = new ConcurrentHashMap<>();
    }

    protected void addObject(K key, V value) {
        if (!this.map.containsKey(key)) {
            this.map.put(key, new ConcurrentLinkedQueue<>());
        }
        this.map.get(key).add(value);
    }

    protected void clearAll(K key) {
        if (!this.map.containsKey(key)) {
            return;
        }
        this.map.remove(key);
    }

    protected boolean hasItems(K key) {
        return !this.map.get(key).isEmpty();
    }

    @NotNull
    protected V getNext(K key) {
        return this.map.get(key).poll();
    }

}
