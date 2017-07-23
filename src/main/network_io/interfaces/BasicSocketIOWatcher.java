package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionIdEventArg;
import utils.SocketDataEventArg;

public interface BasicSocketIOWatcher {
    void onDisconnect(@NotNull ConnectionIdEventArg arg);
    void onData(@NotNull SocketDataEventArg arg);
}
