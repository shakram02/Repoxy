package utils;

import java.util.LinkedList;

public class LimitedSizeQueue<K> extends LinkedList<K> {

    private int maxSize;

    public LimitedSizeQueue(int size) {
        this.maxSize = size;
    }

    public boolean add(K k) {
        int size = this.size();
        if (size + 1 > maxSize) {
            this.removeFirst();
        }
        return super.add(k);
    }

    public K getYongest() {
        return get(this.size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
}