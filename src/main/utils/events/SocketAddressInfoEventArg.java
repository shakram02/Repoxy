package tests.utils.events;

import org.immutables.value.Value;
import tests.utils.ConnectionId;
import tests.utils.SenderType;

@Value.Immutable
public abstract class SocketAddressInfoEventArg extends SocketEventArguments {
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
