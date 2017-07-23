package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.ConnectionIdEventArg;
import utils.SocketDataEventArg;

import java.io.IOException;

public interface BasicSocketIOCommands {
    void sendData(@NotNull SocketDataEventArg arg);

    void closeConnection(@NotNull ConnectionIdEventArg arg);

    void cycle() throws IOException;

    boolean isReceiverAlive(@NotNull ConnectionIdEventArg arg);

    @NotNull
    String getConnectionInfo(@NotNull ConnectionId id);
}
