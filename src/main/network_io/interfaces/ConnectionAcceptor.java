package network_io.interfaces;

import utils.SocketEventArg;

public interface ConnectionAcceptor extends BasicSocketIOWatcher {
    void onConnectionAccepted(SocketEventArg arg);
}
