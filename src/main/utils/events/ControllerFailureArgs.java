package tests.utils.events;

import org.immutables.value.Value;
import tests.utils.ConnectionId;
import tests.utils.SenderType;

@Value.Immutable
public abstract class ControllerFailureArgs extends SocketEventArguments {

    @Override
    public ConnectionId getId() {
        throw new IllegalStateException();
    }

    @Override
    @Value.Lazy
    public SenderType getSenderType() {
        return SenderType.Mediator;
    }

    @Override
    @Value.Lazy
    public EventType getReplyType() {
        return EventType.ChangeController;
    }
}
