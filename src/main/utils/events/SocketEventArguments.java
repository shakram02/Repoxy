package utils.events;

import utils.SenderType;

public interface SocketEventArguments extends Cloneable {
    SenderType getSenderType();

    EventType getReplyType();

    long getTimeStamp();

    SocketEventArguments createRedirectedCopy(SenderType newSender);

    SocketEventArguments clone();
}
