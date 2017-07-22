package utils;

import java.util.List;

/**
 * Socket Event args for better event dispatching
 */
public class SocketEventArg {
    private long timeStamp;
    private EventType replyType;
    private ConnectionId id;
    private List<Byte> extraData;
    private SenderType senderType;

    /**
     * Redirection constructor, flips DataIn event to SendTo
     *
     * @param newOriginator Redirecting object type
     * @param old           Old event arguments
     */
    public static SocketEventArg Redirect(SenderType newOriginator, SocketEventArg old) {
        SocketEventArg redirected = new SocketEventArg();
        redirected.timeStamp = old.timeStamp;
        redirected.replyType = old.replyType;
        redirected.id = old.id;
        redirected.extraData = old.extraData;
        redirected.senderType = newOriginator;

        return redirected;
    }

    private SocketEventArg() {
    }

    public SocketEventArg(SenderType senderType,
                          EventType eventType, ConnectionId id) {

        this.senderType = senderType;
        this.replyType = eventType;
        this.id = id;
    }

    public ConnectionId getId() {
        return id;
    }

    public List<Byte> getExtraData() {
        return extraData;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public SocketEventArg(SenderType senderType,
                          EventType replyType, ConnectionId id, List<Byte> extraData) {

        this.senderType = senderType;
        this.replyType = replyType;
        this.id = id;
        this.extraData = extraData;
        this.timeStamp = System.currentTimeMillis();
    }

    public EventType getReplyType() {
        return replyType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return String.format("ID: [%s] %s FROM %s", this.id, this.replyType, this.senderType);
    }
}
