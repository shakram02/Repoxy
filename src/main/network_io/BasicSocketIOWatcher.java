package network_io;

import utils.SocketEventArg;

public interface BasicSocketIOWatcher {
    void onDisconnect(SocketEventArg arg);

    void onData(SocketEventArg arg);
}
