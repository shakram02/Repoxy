package tests.utils;

import org.immutables.value.Value;

import java.util.Objects;

/**
 * Each new connection is given a unique ID to be used in address translation
 */
@Value.Immutable
public abstract class ConnectionId {
    private static long MAX_ID = 0;
    private long id;

    @Value.Check
    protected void setId() {
        // FIXME This is a HACK to set the correct ID
        this.id = MAX_ID++;

    }

    public static ConnectionId CreateForTesting(int id) {
        ConnectionId falseId = tests.utils.ImmutableConnectionId.builder().build();
        falseId.id = id;
        return falseId;
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
