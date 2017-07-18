package base_classes;

/**
 * Each new connection is given a unique ID to be used in address translation
 */
public class ConnectionId {
    private static int MAX_ID = 0;
    private int id;

    public ConnectionId() {
        this.id = MAX_ID;
        MAX_ID++;

        if (MAX_ID == Integer.MAX_VALUE) {
            throw new RuntimeException("out of IDs");
        }

    }

    public static ConnectionId CreateForTesting(int id) {
        ConnectionId falseId = new ConnectionId();
        falseId.id = id;
        MAX_ID--;
        return falseId;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id + "";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConnectionId && this.id == ((ConnectionId) o).id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
