package utils;

import java.util.Objects;

/**
 * Each new connection is given a unique ID to be used in address translation
 */
public class ConnectionId {
    private static long MAX_ID = 0;
    private long id;

    private ConnectionId() {
        this.id = ConnectionId.MAX_ID;

    }

    public static ConnectionId CreateNext() {
        ConnectionId ret = new ConnectionId();
        ConnectionId.UpdateMaxId();
        return ret;
    }

    public static ConnectionId CreateForTesting(int id) {
        ConnectionId falseId = new ConnectionId();
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
