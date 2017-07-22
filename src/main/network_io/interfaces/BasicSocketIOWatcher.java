package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.SocketEventArg;

public interface BasicSocketIOWatcher {
    void onDisconnect(@NotNull SocketEventArg arg);

    void onData(@NotNull SocketEventArg arg);
}
