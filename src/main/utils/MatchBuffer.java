package utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiPredicate;

public class MatchBuffer<T> {
    ConcurrentLinkedQueue<T> collection;
    private final BiPredicate<T, T> biPredicate;

    public MatchBuffer(BiPredicate<T, T> biPredicate) {
        this.collection = new ConcurrentLinkedQueue<>();
        this.biPredicate = biPredicate;
    }

    public Optional<T> addIfMatchNotFound(T data) {
        Objects.requireNonNull(data);
        Iterator<T> iter = collection.iterator();

        while (iter.hasNext()) {
            T nextItem = iter.next();
            if (this.biPredicate.test(data, nextItem)) {
                iter.remove();
                return Optional.of(nextItem);
            }
        }

        this.collection.add(data);

        return Optional.empty();
    }

    public int getSize() {
        return collection.size();
    }

    public Collection<T> getCollection() {
        return collection;
    }
}
