package utils;

import java.util.List;

/**
 * Socket Event args for better event dispatching
 */
public class SocketEventArg {
    public EventType eventType;
    public ConnectionId id;
    public List<Byte> extraData;
    public SenderType senderType;

    public SocketEventArg(SenderType senderType,
                          EventType eventType, ConnectionId id) {

        this.senderType = senderType;
        this.eventType = eventType;
        this.id = id;
    }

    public SocketEventArg(SenderType senderType,
                          EventType eventType, ConnectionId id, List<Byte> extraData) {

        this.senderType = senderType;
        this.eventType = eventType;
        this.id = id;
        this.extraData = extraData;
    }
}
