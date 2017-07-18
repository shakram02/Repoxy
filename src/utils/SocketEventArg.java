package utils;

import java.util.List;

/**
 * Socket Event args for better event dispatching
 */
public class SocketEventArg {
    public EventType type;
    public ConnectionId id;
    public List<Byte> extraData;

    public SocketEventArg(EventType type, ConnectionId id) {

        this.type = type;
        this.id = id;
    }

    public SocketEventArg(EventType type, ConnectionId id, List<Byte> extraData) {

        this.type = type;
        this.id = id;
        this.extraData = extraData;
    }
}
