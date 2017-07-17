package base_classes;

/**
 * Each new connection is given a unique ID to be used in address translation
 */
public class ConnectionId {
    private static long MAX_ID = 0;
    private long id;

    public ConnectionId() {
        this.id = MAX_ID;
        MAX_ID++;

    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id + "";
    }
}
