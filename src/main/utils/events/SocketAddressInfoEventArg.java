package utils.events;

import org.immutables.value.Value;
import utils.ConnectionId;
import utils.SenderType;

@Value.Immutable
public abstract class SocketAddressInfoEventArg implements SocketEventArguments {
    public abstract String getIp();
    public abstract int getPort();

    @Override
    public SenderType getSenderType() {
        throw new IllegalStateException();
    }

    @Override
    public ConnectionId getId() {
        throw new IllegalStateException();
    }

    @Override
    public EventType getReplyType() {
        return EventType.ChangeController;
    }
}
