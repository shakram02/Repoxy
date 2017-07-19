package utils;

public class EventBuffer extends QueueMap<ConnectionId, SocketEventArg> {

    public void addEvent(ConnectionId id, SocketEventArg arg) {
        super.addObject(id, arg);
    }

    public void clearAllEvents(ConnectionId id) {
        super.clearAll(id);
    }

    public boolean hasEvents(ConnectionId id) {
        return super.hasItems(id);
    }

    public SocketEventArg getEvent(ConnectionId id) {
        return super.getNext(id);
    }
}
