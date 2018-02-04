package utils.events;

import utils.ConnectionId;
import utils.MonotonicClock;
import utils.SenderType;

public abstract class SocketEventArguments {
    private final long timestamp;

    public abstract ConnectionId getId();

    public abstract SenderType getSenderType();

    public SocketEventArguments() {
        this.timestamp = MonotonicClock.getTimeMillis();
    }


    public final long getTimestamp() {
        return timestamp;
    }

    public abstract EventType getReplyType();

    @Override
    public String toString() {
        return String.format("ConnId:[%s] , Sender:[%s] , Type:[%s]\n", getId(), getSenderType(), getReplyType());
    }
}
