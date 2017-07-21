package network_io.interfaces;

import utils.SocketEventArg;

public interface ConnectionCreator extends BasicSocketIOWatcher {
    void connectTo(SocketEventArg args);
}
