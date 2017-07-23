package network_io.interfaces;

import utils.ConnectionIdEventArg;

public interface ConnectionCreator extends BasicSocketIOWatcher {
    void connectTo(ConnectionIdEventArg args);
}
