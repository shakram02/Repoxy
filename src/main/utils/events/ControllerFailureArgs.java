package utils.events;

import org.immutables.value.Value;
import utils.SenderType;

@Value.Immutable
public abstract class ControllerFailureArgs implements SocketEventArguments {


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
