package base_classes;

import java.util.List;

/**
 * Socket Event args for Pub Sub
 */
public class SocketEventArgs {
    EventType type;
    List<Byte> extraData;

    public SocketEventArgs(EventType type) {

        this.type = type;
    }

    public SocketEventArgs(EventType type, List<Byte> extraData) {

        this.type = type;
        this.extraData = extraData;
    }
}
