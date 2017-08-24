package utils;

import org.immutables.value.Value;

import java.util.Objects;

/**
 * Each new connection is given a unique ID to be used in address translation
 */
@Value.Immutable
public abstract class ConnectionId {
    private static long MAX_ID = 0;
    private long id;

    @Value.Lazy
    public long getId() {
        return MAX_ID++;
    }


    public static ConnectionId CreateNext() {
        ConnectionId ret = utils.ImmutableConnectionId.builder().build();
        ConnectionId.UpdateMaxId();
        return ret;
    }

    public static ConnectionId CreateForTesting(int id) {
        ConnectionId falseId = utils.ImmutableConnectionId.builder().build();
        falseId.id = id;
        return falseId;
    }

    private static void UpdateMaxId() {
        MAX_ID++;
        if (MAX_ID == Integer.MAX_VALUE) {
            throw new RuntimeException("Out of IDs");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConnectionId && Objects.equals(this.id, ((ConnectionId) o).id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }
}
