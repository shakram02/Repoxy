package utils.events;

import utils.ConnectionId;
import utils.SenderType;

/**
 * Socket Event args for better event dispatching
 */
public class ConnectionIdEventArg extends BasicSocketEventArg {

    protected final ConnectionId id;

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
