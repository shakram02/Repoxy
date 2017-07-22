package network_io.interfaces;

import utils.SocketEventArg;

import java.io.IOException;

public interface BasicSocketIOCommands {
    void sendData(SocketEventArg arg);

    void closeConnection(SocketEventArg arg);

    void cycle() throws IOException;

    boolean isReceiverAlive(SocketEventArg arg);
}
