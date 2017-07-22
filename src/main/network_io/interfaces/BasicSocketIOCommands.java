package network_io.interfaces;

import org.jetbrains.annotations.NotNull;
import utils.ConnectionId;
import utils.SocketEventArg;

import java.io.IOException;

public interface BasicSocketIOCommands {
    void sendData(@NotNull SocketEventArg arg);

    void closeConnection(@NotNull SocketEventArg arg);

    void cycle() throws IOException;

    boolean isReceiverAlive(@NotNull SocketEventArg arg);

    @NotNull
    String getConnectionInfo(@NotNull ConnectionId id);
}
