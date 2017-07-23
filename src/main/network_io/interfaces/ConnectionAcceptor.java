package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionIdEventArg;

public interface ConnectionAcceptor extends BasicSocketIOWatcher {
    void onConnectionAccepted(@NotNull ConnectionIdEventArg arg);
}
