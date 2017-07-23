package utils;

public interface SocketEventArguments extends Cloneable {
    SenderType getSenderType();

    EventType getReplyType();

    long getTimeStamp();

    SocketEventArguments createRedirectedCopy(SenderType newSender);

    SocketEventArguments clone();
}
