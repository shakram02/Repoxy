package middleware.blocking.io_synchronizer;

import utils.events.SocketDataEventArg;

import java.util.Optional;

public interface Synchronizer {
    void addUnSynchronized(SocketDataEventArg arg);

    Optional<SocketDataEventArg> getSynced();
}
