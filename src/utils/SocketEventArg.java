package utils;

import java.util.List;

/**
 * Socket Event args for better event dispatching
 */
public class SocketEventArg {
    private EventType eventType;
    private ConnectionId id;
    private List<Byte> extraData;
    private SenderType senderType;

    public SocketEventArg(SenderType senderType,
                          EventType eventType, ConnectionId id) {

        this.senderType = senderType;
        this.eventType = eventType;
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
                          EventType eventType, ConnectionId id, List<Byte> extraData) {

        this.senderType = senderType;
        this.eventType = eventType;
        this.id = id;
        this.extraData = extraData;
    }

    public EventType getEventType() {
        return eventType;
    }
}
