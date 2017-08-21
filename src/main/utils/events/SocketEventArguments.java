package utils.events;

import org.immutables.value.Value;
import utils.ConnectionId;
import utils.SenderType;

public interface SocketEventArguments {
    ConnectionId getId();

    SenderType getSenderType();

    @Value.Lazy
    default long getTimestamp() {
        return System.currentTimeMillis();
    }

    EventType getReplyType();
}
