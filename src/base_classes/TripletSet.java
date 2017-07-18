package base_classes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

/**
 * Holds triplet values of ConnectionId,SocketAddress,SelectableChannel
 * that are unique to each connected element
 */
public class TripletSet<K, U, T> {
    private BiMap<K, U> uValues;
    private BiMap<K, T> tValues;

    protected TripletSet() {
        this.tValues = HashBiMap.create();
        this.uValues = HashBiMap.create();
    }

    @NotNull
    protected K getKeyByFirst(U val) {
        return this.uValues.inverse().get(val);
    }

    @NotNull
    protected K getKeyBySecond(T val) {
        return this.tValues.inverse().get(val);
    }

    @NotNull
    protected U getFirst(K key) {
        return this.uValues.get(key);
    }

    @NotNull
    protected T getSecond(K key) {
        return this.tValues.get(key);
    }

    protected synchronized void insert(K key, U first, T second) {
        if (this.uValues.inverse().containsKey(first)) {
            throw new IllegalArgumentException("First value already exists; " + first);
        }

        if (this.tValues.inverse().containsKey(second)) {
            throw new IllegalArgumentException("Second value already exists; " + second);
        }

        this.uValues.put(key, first);
        this.tValues.put(key, second);
    }

    protected synchronized void remove(K key) {
        this.uValues.remove(key);
        this.tValues.remove(key);
    }
}
