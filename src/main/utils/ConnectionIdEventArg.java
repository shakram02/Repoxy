package utils;

import org.jetbrains.annotations.NotNull;

/**
 * Socket Event args for better event dispatching
 */
public class ConnectionIdEventArg extends BasicSocketEventArg {

    private ConnectionId id;


    /**
     * Returns new copy of {@link ConnectionIdEventArg}
     * and setting the sender as the newSender
     *
     * @param newSender Redirecting object type
     * @param old       Old event arguments
     */
    protected static ConnectionIdEventArg createRedirectedCopy(SenderType newSender, @NotNull ConnectionIdEventArg old) {
        ConnectionIdEventArg redirectedCopy = (ConnectionIdEventArg) old.createRedirectedCopy(newSender);
        redirectedCopy.id = old.id;
        return redirectedCopy;
    }

    private ConnectionIdEventArg(BasicSocketEventArg arg) {
        super(arg.senderType, arg.replyType);
    }

    public ConnectionIdEventArg(SenderType senderType,
                                EventType eventType, ConnectionId id) {
        super(senderType, eventType);
        this.id = id;
    }

    public ConnectionId getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID: [%s] %s FROM %s", this.id, this.replyType, this.senderType);
    }
}
