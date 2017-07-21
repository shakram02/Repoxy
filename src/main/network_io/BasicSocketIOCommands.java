package network_io;

import utils.ConnectionId;
import utils.SocketEventArg;

public interface BasicSocketIOCommands {
    void sendData(SocketEventArg arg);

    void closeConnection(ConnectionId arg);
    String getConnectionInfo(ConnectionId id);
}
