package network_io;

import utils.SocketEventArg;

public interface ConnectionAcceptor extends BasicSocketIOWatcher {
    void onConnectionAccepted(SocketEventArg arg);
}
