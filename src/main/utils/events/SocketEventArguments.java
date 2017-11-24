package tests.utils.events;

import org.immutables.value.Value;
import tests.utils.ConnectionId;
import tests.utils.SenderType;

public abstract class SocketEventArguments {
    public abstract ConnectionId getId();

    public abstract SenderType getSenderType();

    @Value.Lazy
    public long getTimestamp() {
        return System.nanoTime();
    }

    public abstract EventType getReplyType();

    @Override
    public String toString() {
        return String.format("ConnId:[%s] , Sender:[%s] , Type:[%s]\n", getId(), getSenderType(), getReplyType());
    }
}
