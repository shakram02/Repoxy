package utils.events;

public interface SocketEventEmitter {
    void register(SocketEventObserver observer);
}
