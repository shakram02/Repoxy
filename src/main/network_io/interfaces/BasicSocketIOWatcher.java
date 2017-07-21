package network_io.interfaces;

import utils.SocketEventArg;

public interface BasicSocketIOWatcher {
    void onDisconnect(SocketEventArg arg);

    void onData(SocketEventArg arg);
}
