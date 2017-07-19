package utils;

import java.util.Objects;

/**
 * Each new connection is given a unique ID to be used in address translation
 */
public class ConnectionId {
    private static String MAX_ID = "a";
    private String id;

    private ConnectionId() {
        this.id = ConnectionId.MAX_ID;

    }

    public static ConnectionId CreateNext() {
        ConnectionId ret = new ConnectionId();
        ConnectionId.UpdateMaxId();
        return ret;
    }

    public static ConnectionId CreateForTesting(String id) {
        ConnectionId falseId = new ConnectionId();
        falseId.id = id;
        return falseId;
    }

    private static void UpdateMaxId() {
        char last = ConnectionId.MAX_ID.charAt(MAX_ID.length() - 1);
        if (last == 'z') {
            ConnectionId.MAX_ID += 'a';
        } else {
            String str = ConnectionId.MAX_ID.substring(0, MAX_ID.length() - 1);
            MAX_ID = str + (last + 1);
        }
    }

    @Override
    public String toString() {
        return this.id + "";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConnectionId && Objects.equals(this.id, ((ConnectionId) o).id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
