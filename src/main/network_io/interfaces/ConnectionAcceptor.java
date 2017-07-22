package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.SocketEventArg;

public interface ConnectionAcceptor extends BasicSocketIOWatcher {
    void onConnectionAccepted(@NotNull SocketEventArg arg);
}
